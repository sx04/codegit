package com.cetcbigdata.varanus.service;


import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.dao.ProjectinfoDAO;
import com.cetcbigdata.varanus.dao.SysUserDAO;
import com.cetcbigdata.varanus.dao.TaskInfoDAO;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.entity.TaskQueryEntity;
import com.cetcbigdata.varanus.utils.ExcelUtil;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/8/17 9:30
 */

@Service
public class TaskService {

    @Autowired
    private TaskInfoDAO taskInfoDAO;

    @Autowired
    private ProjectinfoDAO projectinfoDAO;

    @Autowired
    private SysUserDAO sysUserDAO;

    @Autowired
    private AuditLogService auditLogService;

    @PersistenceContext
    EntityManager em;

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    @Transactional
    public String  taskSave(TaskInfoEntity taskInfoEntity, int userId,MultipartFile file){
        Timestamp creatTime = new Timestamp(new Date().getTime());
        int id = taskInfoEntity.getId();
        if(id>0){
            taskInfoEntity.setUpdateTime(creatTime);
            Timestamp insertTime = taskInfoDAO.insertTime(id);
            taskInfoEntity.setInsertTime(insertTime);
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TASK, "修改",
                        "修改的任务id："+id, "");
            } catch (Exception e) {
                LOG.error("修改" + SysModuleEnum.TASK.getName() + "日志失败！");
            }
        }else{
            taskInfoEntity.setInsertTime(creatTime);
            try {
                String sectionTitle = "";
                String webName = "";
                sectionTitle = taskInfoEntity.getSectionTitle();
                webName = taskInfoEntity.getWebName();
                auditLogService.saveInfo(userId, SysModuleEnum.TASK, "新增",
                        webName+"--"+sectionTitle,"");
            } catch (Exception e) {
                LOG.error("修改" + SysModuleEnum.TASK.getName() + "日志失败！");
            }
        }
        try {
            byte[] data;
            data = file.getBytes();
            taskInfoEntity.setTableInfoImag(data);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("图片插入失败");
        }
        taskInfoDAO.save(taskInfoEntity);
        return "success";
    }

    @Transactional
    public ErrorCode  taskDelete(int i ){
        taskInfoDAO.delete(i);
        return ErrorCode.SUCCESS;
    }

    @Transactional
    public int  taskTake(String[] taskIds,int userId) {
        int num=0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskIds.length; i++) {
            String taskId = taskIds[i];
            sb.append(taskId).append(",");
            int id = Integer.parseInt(taskId);
            taskInfoDAO.taskTake(userId,id);
            num+=1;
        }
        String s=sb.toString();
        String sa =s.substring(0,s.length()-1);
        try {
            auditLogService.saveInfo(userId, SysModuleEnum.TASK, "修改",
                    "领取任务："+sa, "");
        } catch (Exception e) {
            LOG.error("修改" + SysModuleEnum.TASK.getName() + "日志失败！");
        }
        return num;
    }

    public Page<Object> taskQuery(Pageable pageable, TaskQueryEntity taskQueryEntity){

        StringBuilder sql = new StringBuilder(
                "select * from task_info where 1=1");

        String projectName =taskQueryEntity.getProjectName();
        String webName = taskQueryEntity.getWebName();
        String sectionTitle = taskQueryEntity.getSectionTitle();
        String userName = taskQueryEntity.getUserName();
        String isGet =  taskQueryEntity.getIsGet();

        if (StringUtils.isNoneBlank(projectName)) {
            String code = projectinfoDAO.queryProjectByName(projectName);
            sql.append(" and project_id= '").append(code).append("'");
        }

        if (StringUtils.isNoneBlank(webName)) {
            sql.append("  and web_name= '").append(webName).append("'");
        }

        if (StringUtils.isNoneBlank(sectionTitle)) {
            sql.append("  and section_title= '").append(sectionTitle).append("'");
        }

        if (StringUtils.isNoneBlank(taskQueryEntity.getUserName())) {
            String ids =sysUserDAO.queryUserIdByName(userName);
            sql.append("  and get_user_id =").append(ids);

        }

        if (StringUtils.isNoneBlank(taskQueryEntity.getIsGet())) {
            sql.append("  and is_get= '").append(isGet).append("'");
        }
        sql.append(" ORDER BY insert_time");

        return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, TaskInfoEntity.class);
    }

    //查询所有项目名
    public List<String> queryProjectName(){
        return projectinfoDAO.queryProjectName();
    }

    //根据项目名查询查询网站名
    public List<String>  queryWebNameByProject(String s){
        return taskInfoDAO.queryWebNameByProject(s);
    }

    //根据模糊查询查询网站名
    public List<String>  queryWebName(String webName){
        return taskInfoDAO.queryWebName(webName);
    }

    //根据网站查询查询网站下模块名
    public List<String>  querySectiontitle(String webName){
        return taskInfoDAO.querySectiontitle(webName);
    }

    public TaskInfoEntity queryTaskById(int taskId){
        return taskInfoDAO.queryTaskById(taskId);
    }



   //查询工作量
    public Object taskFinishNum(String sd,String ed,String user,String projectName){

        StringBuilder sql = new StringBuilder(
                "select DISTINCT s.user_name,(SELECT count(1) from task_info t where t.state=2 and t.get_user_id =s.id");

        if (StringUtils.isNoneBlank(sd)) {
            sql.append(" and t.state_update_time >'").append(sd).append("'");
        }
        if (StringUtils.isNoneBlank(ed)) {
            sql.append(" and t.state_update_time <'").append(ed).append("'");
        }

        sql.append(") AS num from task_info t  join sys_user s where 1=1");
        if (StringUtils.isNoneBlank(projectName)) {
            String code = projectinfoDAO.queryProjectByName(projectName);
            sql.append(" and project_id= '").append(code).append("'");
        }
        if (StringUtils.isNoneBlank(user)) {
            sql.append(" and s.user_name= '").append(user).append("'");
        }
        String s= sql.toString();
        Query query = em.createNativeQuery(s);
        List us = query.getResultList();
        return us ;
    }


    //Excel导入任务
    @Transactional
    public Boolean importExcel(MultipartFile uploadFile, String projectId) {
        try {
            List<Object[]> list = ExcelUtil.importExcel(uploadFile);
            Timestamp creatTime = new Timestamp(new Date().getTime());
            for (int i = 0; i < list.size(); i++) {
                TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
                taskInfoEntity.setWebName((String) list.get(i)[0]);
                taskInfoEntity.setSectionTitle((String) list.get(i)[1]);
                taskInfoEntity.setSectionUrl((String) list.get(i)[2]);
                taskInfoEntity.setProjectId(projectId);
                taskInfoEntity.setInsertTime(creatTime);
                taskInfoDAO.save(taskInfoEntity);
            }
            LOG.info("导入数据结束。。。。。。");
            return true;
        } catch (Exception e) {
            LOG.info("导入数据失败。。。。。。");
            e.printStackTrace();
        }
        return false;
    }

}
