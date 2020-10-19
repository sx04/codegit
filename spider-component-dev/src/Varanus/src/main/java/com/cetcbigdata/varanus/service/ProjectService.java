package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.dao.ProjectFileDAO;
import com.cetcbigdata.varanus.dao.ProjectinfoDAO;
import com.cetcbigdata.varanus.dao.SysUserDAO;
import com.cetcbigdata.varanus.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */

@Service
public class ProjectService {

    @Autowired
    private ProjectinfoDAO projectinfoDAO;

    @Autowired
    private SysUserDAO sysUserDAO;

    @Autowired
    private ProjectFileDAO projectFileDAO;

    @Autowired
    private EntityManager entityManger;

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    public  List<ProjectBasicEntity> queryProjectList(){
       List<Object> projectList= projectinfoDAO.queryProjectList();
       List<ProjectBasicEntity> projectBasicEntities = new ArrayList<ProjectBasicEntity>();
       try {
           for (int i = 0; i < projectList.size(); i++) {
               ProjectBasicEntity projectBasicEntity = new ProjectBasicEntity();
               Object[] obj = (Object[]) projectList.get(i);
               projectBasicEntity.setCode(obj[0].toString());
               projectBasicEntity.setName(obj[1].toString());
               projectBasicEntity.setDispatchTime(obj[2].toString());
               projectBasicEntity.setPriorLevel(obj[3].toString());
               projectBasicEntity.setUsers(obj[4].toString());
               projectBasicEntity.setIsRun(Integer.parseInt(obj[5].toString()));
               projectBasicEntities.add(projectBasicEntity);
           }
       } catch(Exception e){
           e.printStackTrace();
       }
       return projectBasicEntities;
   }

    @Transactional
    public ErrorCode addProject(List<UserEntity> sysUsers,ProjectInfoEntity projectInfoEntity){
        Timestamp creatTime = new Timestamp(new Date().getTime());
        projectInfoEntity.setCreateTime(creatTime);
        int table = projectInfoEntity.getIsTable();
        String code = projectInfoEntity.getCode();
        StringBuilder tableDataName = new StringBuilder("detail_"+code+"_data");
        StringBuilder tableConfigName = new StringBuilder("detail_"+code+"_config");
        try {
            //为表格类项目
            if(table>0) {
                LOG.info("表格类项目不建表");
            }
            else{
                projectInfoEntity.setDataTableCode(tableDataName.toString());
                //创建detail_xxx_config表，如果该表存在则不建表
                StringBuilder configSql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableConfigName);
                configSql.append("(\n" +
                        "id int PRIMARY KEY auto_increment,\n" +
                        "list_id int NOT NULL,\n" +
                        "client_type varchar(255) ,\n" +
                        "request_type varchar(255) ,\n" +
                        "request_params varchar(255),\n" +
                        "response_type varchar(255),\n" +
                        "json_field varchar(255),\n" +
                        "need_proxy int,\n" +
                        "test_url text,\n" +
                        "analyze_type int,\n" +
                        "title varchar(255),\n" +
                        "site_width int,\n" +
                        "source_html varchar(255),\n" +
                        "interface_id int)");
                String s1 = configSql.toString();
                Query query1 = entityManger.createNativeQuery(s1);
                query1.executeUpdate();
                //创建detail_xxx_data表
                StringBuilder dataSql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableDataName);
                dataSql.append("(\n" +
                        "id varchar(32) PRIMARY KEY,\n" +
                        "list_id int NOT NULL,\n" +
                        "template_id int NOT NULL,\n" +
                        "insert_date datetime,\n" +
                        "update_date datetime,\n" +
                        "repeat_count int,\n" +
                        "site_width int,\n" +
                        "is_clean int(1) NOT NULL ,\n" +
                        "is_file int(1),\n" +
                        "url text NOT NULL,\n" +
                        "source_html varchar(255),\n" +
                        "title varchar(255))");
                String s2 = dataSql.toString();
                Query query2 = entityManger.createNativeQuery(s2);
                query2.executeUpdate();
            }
            //保存项目信息
            projectinfoDAO.save(projectInfoEntity);
            //保存项目分配用户信息（先删除之前的）
            sysUserDAO.deleteProjectUser(code);
            for (int i = 0; i < sysUsers.size(); i++) {
                UserEntity userEntity = sysUsers.get(i);
                int id = userEntity.getId();
                sysUserDAO.addProjectUser(id,code);
            }
            return ErrorCode.SUCCESS;
        } catch(Exception e){
            LOG.error("项目创建失败");
            return ErrorCode.PROJECT_CREATE_ERROR;
        }
    }


    @Transactional
    public int deleteProject(String code){
        return projectinfoDAO.deleteProject(code);
    }

    @Transactional
    public int projectStart(String code,int isRun){
        Timestamp creatTime = new Timestamp(new Date().getTime());
        return projectinfoDAO.projectStart(code,isRun,creatTime);
    }

    public ProjectInfoEntity queryProjectOne(String code){
        return projectinfoDAO.queryProjectOne(code);
    }


    public List<UserEntity> queryProjectUser(String code){
        List<UserEntity> sysUser= sysUserDAO.queryProjectUsers(code);
        return sysUser;
    }

    public List<ProjectFileEntity> queryProjectFileEntities(String code){
        List<ProjectFileEntity> ProjectFileEntities= projectFileDAO.findProjectFieldEntitiesByProjectId(code);
        return ProjectFileEntities;
    }


    @Transactional
    public int updateProjectInfo(ProjectInfoEntity projectInfoEntity){
        String code = projectInfoEntity.getCode();
        String projectName = projectInfoEntity.getName();
        int prior = projectInfoEntity.getPriorLevel();
        int isCanStart = projectInfoEntity.getIsCanStart();
        int isTable = projectInfoEntity.getIsTable();
        String dispatchTime = projectInfoEntity.getDispatchTime();
        return projectinfoDAO.updateProjectInfo(code,projectName,prior,isCanStart,dispatchTime,isTable);
    }

    @Transactional
    public int  projectUserUpdate(String code,String[] usersId) {
        int nn = sysUserDAO.deleteProjectUser(code);
        int num=0;
        for (int i = 0; i < usersId.length; i++) {
            String userId = usersId[i];
            int id = Integer.parseInt(userId);
            sysUserDAO.addProjectUser(id,code);
            num+=1;
        }
        return num;
    }
}
