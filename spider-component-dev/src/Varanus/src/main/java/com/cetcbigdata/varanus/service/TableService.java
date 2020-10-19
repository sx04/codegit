package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.dao.ProjectFileDAO;
import com.cetcbigdata.varanus.dao.TableColumnInfoDAO;
import com.cetcbigdata.varanus.dao.TableDetailConfigDAO;
import com.cetcbigdata.varanus.dao.TaskInfoDAO;
import com.cetcbigdata.varanus.entity.TableColumnInfoEntity;
import com.cetcbigdata.varanus.entity.TableDetailConfigEntity;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.parser.WebClientDetailParser;
import com.cetcbigdata.varanus.parser.WebDriverDetailParser;
import com.cetcbigdata.varanus.utils.HumpToLine;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:28
 */
@Service
public class TableService {

    @Autowired
    private TableColumnInfoDAO tableColumnInfoDao;

    @Autowired
    private TaskInfoDAO taskInfoDAO;


    @Autowired
    private ProjectFileDAO projectFieldDAO;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TableDetailConfigDAO tableDetailConfigDao;

    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    private WebDriverDetailParser webDriverDetailParser;

    @PersistenceContext
    EntityManager entityManger;

    ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    private static final Logger LOG = LoggerFactory.getLogger(TableService.class);

    //查询表格类模板第一个Tab页
    public TaskInfoEntity tableOneBasic(int taskId) {
        TaskInfoEntity taskInfoEntity = taskInfoDAO.findOne(taskId);
        return taskInfoEntity;
    }

    //查询表格类模板第二个Tab页
    public Object tableDataExtraction(int taskId) {
        return tableDetailConfigDao.findTableByTaskId(taskId);
    }

    //查询表格类模板第三个Tab页
    public Object tableDataStorage(int taskId) {
        return tableColumnInfoDao.findColumnByTaskId(taskId);
    }

    //保存表格类模板第一个Tab页
    @Transactional
    public Object tableBasicSave(TaskInfoEntity taskInfoEntity,int userId){
        int taskId = taskInfoEntity.getId();
        String srcTypeCode = taskInfoEntity.getSrcTypeCode();
        String domainCode = taskInfoEntity.getDomainCode();
        try {
            //保存任务字段
            taskInfoDAO.saveTaskInfo(domainCode,srcTypeCode,taskId);
            auditLogService.saveInfo(userId, SysModuleEnum.TASK, "修改",
                            "修改id为"+taskId+"的任务基本信息","");
            return ErrorCode.SUCCESS;
            } catch(Exception e){
                    LOG.error("修改"+SysModuleEnum.TASK.getName()+"日志失败！");
            }
        return ErrorCode.TASK_SAVE_ERROR;
    }


    //保存表格类模板第二个Tab页
    @Transactional
    public Object tableDataExtractionSave(TableDetailConfigEntity tableDetailConfigEntity,int userId) {
        int id = tableDetailConfigDao.saveAndFlush(tableDetailConfigEntity).getId();
        //将该表格模板所属的任务状态设置为1配置中
        taskInfoDAO.setTableTaskState(1,id);
        int taskId = tableDetailConfigEntity.getTaskId();
        //根据任务id查询对应项目
        String projectId = taskInfoDAO.queryProjectIdByTaskId(taskId);
        //拼接表名并查询该表是否存在，若不存在说明为新项目需要建表，若存在则不需要建表
        StringBuilder tableName = new StringBuilder("CREATE TABLE IF NOT EXISTS 'table_data_");
        tableName.append(projectId).append("_").append(taskId).append("'(")
                .append("id int PRIMARY KEY auto_increment," +
                        "task_id int NOT NULL," +
                        "insert_date datetime)");
        String tableName1 = tableName.toString();
        Query query = entityManger.createNativeQuery(tableName1);
        query.executeUpdate();
        try {
            auditLogService.saveInfo(userId, SysModuleEnum.TABLE, "修改",
                    "修改id为"+id+"的表格配置信息","");
            return  ErrorCode.SUCCESS;
        } catch(Exception e){
            LOG.error("修改"+SysModuleEnum.TABLE.getName()+"日志失败！");
            return  ErrorCode.TABLE_SAVE_ERROR;
        }
    }


    //表格类模板第三个Tab页中列表信息新增
    @Transactional
    public Object tableDataStorageAdd(TableColumnInfoEntity tableColumnInfoEntity, int userId) {
        if (tableColumnInfoEntity != null) {
            //列信息表新增数据
            tableColumnInfoDao.save(tableColumnInfoEntity);
            //根据所需列向数据表中添加列
            int taskId = tableColumnInfoEntity.getTaskId();
            //根据任务id查询对应项目
            String projectId = taskInfoDAO.queryProjectIdByTaskId(taskId);
            StringBuilder tableName = new StringBuilder("table_data_");
            String name = tableName.append(projectId).append("_").append(taskId).toString();
            //查询是否有数据存储表
            String i1 = projectFieldDAO.findDetailTable(name);
            String code = tableColumnInfoEntity.getColumnCode();
            String type = tableColumnInfoEntity.getType();
            if (i1 != null && i1.length() != 0) {
                //新增数据表的列
                StringBuilder addDataRow = new StringBuilder("alter table " + name);
                String s = HumpToLine.humpToLine(code);
                addDataRow.append(" add " + s +" "+ type);
                Query query = entityManger.createNativeQuery(addDataRow.toString());
                query.executeUpdate();
            }
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TABLE, "新增",
                        taskId + "任务数据存储表新增表字段：" + code, "");
            } catch (Exception e) {
                LOG.error("新增" + SysModuleEnum.TABLE.getName() + "日志失败！");
            }
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.TABLE_COLUMN_ADD_ERROR;
    }

    //表格类模板第三个Tab页中列表信息删除
    @Transactional
    public Object tableDataExtractionDelete(int id, int userId) {
        if (id != 0) {
            tableDetailConfigDao.delete(id);
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TABLE, "删除",
                         "删除表table_detail_config中id为" +id+"的数据", "");
            } catch (Exception e) {
                LOG.error("删除" + SysModuleEnum.TABLE.getName() + "日志失败！");
            }
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.TABLE_COLUMN_DELETE_ERROR;
    }


    //表格类模板第三个Tab页中列表信息删除
    @Transactional
    public Object tableDataStorageDelete(int id, int userId) {
        if (id != 0) {
            //根据任务id查询对应项目
            int taskId = tableColumnInfoDao.findTaskIdById(id);
            String projectId = taskInfoDAO.queryProjectIdByTaskId(taskId);
            StringBuilder tableName = new StringBuilder("table_data_");
            String name = tableName.append(projectId).append("_").append(taskId).toString();
            //查询是否有数据存储表
            String i1 = projectFieldDAO.findDetailTable(name);
            String code = tableColumnInfoDao.findOne(id).getColumnCode();
            //根据所需列向数据表中删列
            if (i1 != null && i1.length() != 0) {
                //删除数据表的列
                StringBuilder deleteConfigRow = new StringBuilder("alter table " + name);
                String s = HumpToLine.humpToLine(code);
                deleteConfigRow.append(" drop column " +s);
                Query query = entityManger.createNativeQuery(deleteConfigRow.toString());
                query.executeUpdate();
            }
            //列信息表删除数据
            tableColumnInfoDao.delete(id);
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TABLE, "删除",
                        taskId + "任务数据存储表删除表字段：" + code, "");
            } catch (Exception e) {
                LOG.error("删除" + SysModuleEnum.TABLE.getName() + "日志失败！");
            }
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.TABLE_COLUMN_DELETE_ERROR;
    }

    //表格类模板第二个Tab页的验证
    public Object tableDataExtractionVerify(TableDetailConfigEntity tableDetailConfigEntity) {
        try {
            webDriverThreadLocal.set(webDriverFactory.get());
            webDriverThreadLocal.get().get(tableDetailConfigEntity.getTestUrl());
            Thread.sleep(3000);
            return webDriverDetailParser.verifyTable(tableDetailConfigEntity,webDriverThreadLocal.get());
        } catch (Exception e) {
            LOG.error("验证详情出错", e);
            return " ";
        } finally {
            WebDriver webDriver = webDriverThreadLocal.get();
            if (webDriver != null) {
                webDriverFactory.close(webDriverThreadLocal.get());
            }
            webDriverThreadLocal.remove();
        }
    }
   //表格类爬取数据清除
    @Transactional
    public ErrorCode tableDataClean(int taskId, int userId) {

        String projectId = taskInfoDAO.queryProjectIdByTaskId(taskId);
        StringBuilder tableName = new StringBuilder("table_data_");
        //表格类任务爬取数据存储表名
        String name = tableName.append(projectId).append("_").append(taskId).toString();
       try {
           StringBuilder configSql = new StringBuilder("truncate " + name);
           String s = configSql.toString();
           Query query = entityManger.createNativeQuery(s);
           query.executeUpdate();
           auditLogService.saveInfo(userId, SysModuleEnum.TABLE, "删除",
                   "清空表名为" + name + "的数据表的数据", "");
           return ErrorCode.SUCCESS;
       }
        catch (Exception e) {
        LOG.error("数据清空失败");
        return ErrorCode.DATA_DELETE_FAIL;
       }
    }
}

