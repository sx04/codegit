package com.cetcbigdata.varanus.service;


import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.dao.ProjectFileDAO;
import com.cetcbigdata.varanus.entity.ProjectFileEntity;
import com.cetcbigdata.varanus.utils.HumpToLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
@Service
public class ProjectFileService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectFileService.class);


    @Autowired
    private ProjectFileDAO projectFieldDAO;

    @Autowired
    private EntityManager entityManger;

    @Autowired
    private AuditLogService auditLogService;


    public List<ProjectFileEntity> findProjectFieldEntityByProjectId(String code){
        return projectFieldDAO.findProjectFieldEntitiesByProjectId(code);
    }


    @Transactional
    public ErrorCode deleteProjectFieldEntitiesById(int id,int userId){
        ProjectFileEntity projectFieldEntity = projectFieldDAO.findProjectFieldEntityById(id);
        if (Optional.ofNullable(projectFieldEntity).isPresent()){
            try {
                String projectId = projectFieldEntity.getProjectId();
                StringBuilder tableConfigName = new StringBuilder("detail_" + projectId + "_config");
                StringBuilder tableDataName = new StringBuilder("detail_" + projectId + "_data");
                String i1 = projectFieldDAO.findDetailTable(tableConfigName.toString());
                String i2 = projectFieldDAO.findDetailTable(tableDataName.toString());
                //如果查询出来有该表，则除了删project_field外还需要删detail_config与detail_data这两张表对应的列
                //如果查询出来无该表，则只删project_field表
                String code = projectFieldEntity.getFileCode();
                //删detail_config
                if (i1 != null && i1.length() != 0) {
                    //删除detail_config列
                    StringBuilder deleteConfigRow = new StringBuilder("alter table " + "detail_" + projectId + "_config");
                    String s = HumpToLine.humpToLine(code);
                    deleteConfigRow.append(" drop column " + s + "_xpath");
                    Query query = entityManger.createNativeQuery(deleteConfigRow.toString());
                    query.executeUpdate();
                }
                //删detail_data
                if (i2 != null && i2.length() != 0) {
                    String fieldType = projectFieldEntity.getFileCode();
                    //删除detail_data列
                    StringBuilder deleteDataRow = new StringBuilder("alter table " + "detail_" + projectId + "_data");
                    String s = HumpToLine.humpToLine(code);
                    deleteDataRow.append(" drop column " + s + "_data");
                    Query query = entityManger.createNativeQuery(deleteDataRow.toString());
                    query.executeUpdate();
                }
                projectFieldDAO.deleteProjectFieldEntitiesById(id);
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "删除",
                            projectId+"项目的字段："+code, "");
                } catch (Exception e) {
                    LOG.error("修改" + SysModuleEnum.PROJECT.getName() + "日志失败！");
                }
                return ErrorCode.SUCCESS;
            } catch(Exception e){
                e.printStackTrace();
                LOG.error("删除字段失败");
            }
        }
        LOG.error("表删除字段失败");
        return ErrorCode.PROJECT_FILE_DELETE_FAIL;
    }


    @Transactional
    public ErrorCode  addProjectField(ProjectFileEntity projectFiledEntity,int userId) {

        if (Optional.ofNullable(projectFiledEntity).isPresent()) {
            try {
                String projectId = projectFiledEntity.getProjectId();
                StringBuilder tableConfigName = new StringBuilder("detail_" + projectId + "_config");
                StringBuilder tableDataName = new StringBuilder("detail_" + projectId + "_data");
                String i1 = projectFieldDAO.findDetailTable(tableConfigName.toString());
                String i2 = projectFieldDAO.findDetailTable(tableDataName.toString());
                //如果查询出来有该表，则除了增project_field外还需要增detail_config与detail_data这两张表对应的列
                //如果查询出来无该表，则只增project_field表
                String code = projectFiledEntity.getFileCode();
                //增detail_config
                if (i1 != null && i1.length() != 0) {
                    //增加detail_config列
                    StringBuilder addConfigRow = new StringBuilder("alter table " + "detail_" + projectId + "_config");
                    String s = HumpToLine.humpToLine(code);
                    addConfigRow.append(" add " + s + "_xpath" + " varchar(255)");
                    Query query = entityManger.createNativeQuery(addConfigRow.toString());
                    query.executeUpdate();
                }
                //增detail_data
                if (i2 != null && i2.length() != 0) {
                    String fieldType = projectFiledEntity.getFileType();
                    //增加detail_data列
                    StringBuilder addDataRow = new StringBuilder("alter table " + "detail_" + projectId + "_data");
                    String s = HumpToLine.humpToLine(code);
                    addDataRow.append(" add " + s + "_data " + fieldType);
                    Query query = entityManger.createNativeQuery(addDataRow.toString());
                    query.executeUpdate();
                }
                projectFieldDAO.save(projectFiledEntity);
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "新增",
                            projectId+"项目的字段："+code, "");
                } catch (Exception e) {
                    LOG.error("修改" + SysModuleEnum.PROJECT.getName() + "日志失败！");
                }
            } catch(Exception e){
                e.printStackTrace();
                LOG.error("表增加字段失败");
            }
                return ErrorCode.SUCCESS;
                }
            LOG.error("表增加字段失败");
            return ErrorCode.PROJECT_FILE_CREATE_FAIL;
        }
    }



