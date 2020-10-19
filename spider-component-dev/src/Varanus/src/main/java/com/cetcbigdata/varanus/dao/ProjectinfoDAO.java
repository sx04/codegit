package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
@Repository
public interface ProjectinfoDAO extends JpaRepository<ProjectInfoEntity, Integer> {



    //项目查询
    @Query(value = "select code,name,dispatch_time dispatchTime,prior_level priorLevel,GROUP_CONCAT(su.user_name SEPARATOR ';') users,is_run from project_info pi\n" +
            "join project_user pu on pi.code =pu.project_id\n" +
            "join sys_user su on pu.user_id=su.id\n" +
            "WHERE is_valid='1' group by code ORDER BY create_time DESC", nativeQuery = true)
    List<Object> queryProjectList();

    //项目删除
    @Modifying
    @Query(value = "update project_info set is_valid =0 where code= ?1", nativeQuery = true)
    int deleteProject(String code);

    //项目启动
    @Modifying
    @Query(value = "update project_info set is_run =?2,create_time =?3 where code= ?1", nativeQuery = true)
    int projectStart(String code , int isRun, Timestamp creatTime);


    //获取单一项目信息
    @Query(value = "select * from project_info where code=?1", nativeQuery = true)
    ProjectInfoEntity queryProjectOne(String code);

    //项目基本信息修改
    @Modifying
    @Query(value = "UPDATE project_info set name=?2,prior_level=?3,is_can_start=?4,dispatch_time=?5,is_table=?6 where code=?1", nativeQuery = true)
    int updateProjectInfo(String code,String projectName,int prior,int isCanStart,String dispatchTime,int isTable);

    //根据项目名称获取项目code
    @Query(value = "select code from project_info where name=?1", nativeQuery = true)
    String  queryProjectByName(String name);

    //获取项目code,name
    @Query(value = "select code,name from project_info", nativeQuery = true)
    List queryProjectCodeName();

    //查询项目名称
    @Query(value = "select name from project_info ", nativeQuery = true)
    List<String>  queryProjectName();

    @Query(value = "select p.code,count(t.id) from  task_info t  right join project_info p on  t.project_id=p.code group by t.project_id  ", nativeQuery = true)
    List<Object> projectTaskTotal();

    @Query(value = "select p.code ,count(t.id) from  task_info t join project_info p on  t.project_id=p.code where is_get=1 group by t.project_id  ", nativeQuery = true)
    List<Object> projectTaskGet();

    @Query(value = "select p.code ,count(t.id) from  task_info t  join project_info p on  t.project_id=p.code where state=2 group by t.project_id  ", nativeQuery = true)
    List<Object> projectTaskFinish();

    @Query(value = "select p.code ,count(t.id) from  task_info t join project_info p on  t.project_id=p.code where state in (0,1) group by t.project_id", nativeQuery = true)
    List<Object> projectTaskUnfinish();

    @Query(value = "select count(1) as tempNum from template te join task_info ti on te.task_id=ti.id where ti.project_id=?1", nativeQuery = true)
    int tempNumByprojectId(String projectId);

    @Query(value = "select count(1) as warningNum from task_warning tw join template te on tw.template_id=te.id join task_info ti on ti.id=te.task_id where ti.project_id=?1 ", nativeQuery = true)
    int warningNumByprojectId(String projectId);

    @Query(value = "select count(1) as unGet from task_info where project_id=?1 and is_get=0 ", nativeQuery = true)
    int unGetByprojectId(String projectId);

    List<ProjectInfoEntity> findByIsValid(int isvaild);

}
