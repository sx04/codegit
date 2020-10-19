package com.cetcbigdata.varanus.controller;

import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.service.AuditLogService;
import com.cetcbigdata.varanus.service.ProjectFileService;
import com.cetcbigdata.varanus.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
@RestController
public class ProjectContoller {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFileService projectFiledService;

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectContoller.class);


    //项目保存(保存项目基本信息如果无数据表则创建表)
    @PostMapping("project/add")
    public Object  projectAdd(@RequestBody ProjectInfoUserEntity projectInfoUserEntity) {
        int userId =1;
        ProjectInfoEntity projectInfoEntity = projectInfoUserEntity.getProjectInfoEntity();
        List<UserEntity> sysUsers = projectInfoUserEntity.getSysUsers();
        String code = projectInfoEntity.getCode();
        if (code.matches("^[a-zA-Z]*")) {
            projectInfoEntity.setCreateUserId(userId);
            ErrorCode ec = projectService.addProject(sysUsers, projectInfoEntity);
            if(ec.getCode()==200) {
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "新增",
                            "项目code为" + code, "");
                } catch (Exception e) {
                    LOG.error("修改" + SysModuleEnum.PROJECT.getName() + "日志失败！");
                }
            }
            return ec;
        }
        return ErrorCode.PROJECT_CREATE_ERROR;
    }


    //项目删除
    @GetMapping("project/delete")
    public Object  projectDelete(@RequestParam("code") String code) {
        int i = projectService.deleteProject(code);
        int userId =1;
        if (i > 0) {
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "删除",
                        "项目code为"+code,"");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.PROJECT.getName()+"日志失败！");
            }
            return ErrorCode.SUCCESS;
        } else {
            return ErrorCode.PROJECT_DELETE_ERROR;
        }
    }

    //项目启动
    @PostMapping("project/start")
    public Object projectStart(@RequestParam("code") String code,@RequestParam("isRun") int isRun) {
        int userId =1;
        int i =  projectService.projectStart(code,isRun);
        if (i > 0) {
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "修改",
                        "项目code为"+code+"的启动状态为"+isRun, "");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.PROJECT.getName()+"日志失败！");
            }
            return ErrorCode.SUCCESS;
        } else {
            return ErrorCode.PROJECT_START_ERROR;
        }
    }

    //获取单个项目信息
    @GetMapping("project/one")
    public ProjectInfoUserEntity  queryProjectOne(@RequestParam("code") String code) {
        ProjectInfoEntity projectInfoEntity =  projectService.queryProjectOne(code);
        List<UserEntity> sysUsers =projectService.queryProjectUser(code);
        ProjectInfoUserEntity projectInfoUserEntity = new ProjectInfoUserEntity();
        projectInfoUserEntity.setProjectInfoEntity(projectInfoEntity);
        projectInfoUserEntity.setSysUsers(sysUsers);
        int isTable = projectInfoEntity.getIsTable();
        //如果是非表格类则不需要返回所需字段内容
        if(isTable==0) {
            List<ProjectFileEntity> projectFileEntities = projectService.queryProjectFileEntities(code);
            projectInfoUserEntity.setProjectFileEntities(projectFileEntities);
        }
        return projectInfoUserEntity;
    }

    //查询项目列表
    @GetMapping("project/query")
    public  List<ProjectBasicEntity> queryProjectList() {

        List<ProjectBasicEntity> projectList =  projectService.queryProjectList();

         return projectList;
    }


  /*  //修改项目基本信息
    @PostMapping("project/update")
    public Object projectUpdate(@RequestBody ProjectInfoEntity projectInfoEntity) {
        int userId =1;
        int i = projectService.updateProjectInfo(projectInfoEntity);
        if (i > 0) {
            try {
                String code = projectInfoEntity.getCode();
                auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "修改",
                        "项目code为"+code+"的基本信息", "");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.PROJECT.getName()+"日志失败！");
            }
            return ErrorCode.SUCCESS;
        } else {
            return ErrorCode.PROJECT_EDIT_ERROR;
        }
    }

    //保存项目用户信息
    @GetMapping("project/user/update")
    public Object projectUserUpdate(@RequestParam("code") String code,@RequestParam("usersIds") String[] usersId) {
        int i = projectService.projectUserUpdate(code, usersId);
        if (i > 0) {
            int userId = 1;
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "修改",
                            code+"项目的用户id"+usersId, "");
                } catch (Exception e) {
                    LOG.error("修改" + SysModuleEnum.PROJECT.getName() + "日志失败！");
                }
                return ErrorCode.SUCCESS;
            } else {
                return ErrorCode.PROJECTUSER_SAVE_ERROR;
            }
    }*/


    //获取项目的所需字段
    @GetMapping("project/field/query")
    public List<ProjectFileEntity> projectFieldQuery(@RequestParam("projectId") String projectId){
        List<ProjectFileEntity> projectFieldEntities = projectFiledService.findProjectFieldEntityByProjectId(projectId);
        return projectFieldEntities;
    }


    //项目字段的新增(会修改相应数据库表)
    @PostMapping("project/field/add")
    public Object addProjectField(@RequestBody ProjectFileEntity projectFiledEntity) {
        int userId = 1;
        String fileCode = projectFiledEntity.getFileCode();
        String code = projectFiledEntity.getProjectId();
            projectFiledService.addProjectField(projectFiledEntity,userId);
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.PROJECT, "新增",
                        "项目" + code + "的字段："+fileCode, "");
            } catch (Exception e) {
                LOG.error("修改" + SysModuleEnum.PROJECT.getName() + "日志失败！");
            }
            return ErrorCode.SUCCESS;

    }

    //项目字段的删除(会修改相应数据库表)
    @PostMapping("project/field/delete")
    public Object deleteProjectField(@RequestParam("id") int id) {
        int userId = 1;
        ErrorCode errorCode = projectFiledService.deleteProjectFieldEntitiesById(id,userId);
        return errorCode;
    }

}
