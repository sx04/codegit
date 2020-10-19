package com.cetcbigdata.varanus.dao;


import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TaskInfoDAO extends JpaRepository<TaskInfoEntity, Integer> {

    //任务查询
    @Query(value = "select web_name,section_title,section_url,insert_time,is_get,get_user_id,task_state from task_info ", nativeQuery = true)
    List<TaskInfoEntity> queryTaskInfo();

    //任务领取
    @Modifying
    @Query(value = "update task_info set get_user_id =?1,is_get=1 where id= ?2 ", nativeQuery = true)
    int taskTake(int userId,int id);

    //查找任务插入时间
    @Query(value = "select insert_time from task_info  where id= ?1 ", nativeQuery = true)
    Timestamp insertTime(int id);

    //根据项目名向前台返回网站名
    @Query(value = "select distinct `web_name` from task_info where project_id =(Select code from project_info where name=?1)", nativeQuery = true)
    List<String> queryWebNameByProject(String projectName);

    //根据模糊查询向前台返回网站名
    @Query(value = "select distinct `web_name` from task_info where web_name like %?1%\n", nativeQuery = true)
    List<String> queryWebName(String webName);

    //根据网站名向前台返回板块名
    @Query(value = "select distinct `section_title` from task_info where web_name =?1\n", nativeQuery = true)
    List<String> querySectiontitle(String webName);

    //任务保存来源领域信息
    @Modifying
    @Query(value = "update task_info set domain_code =?1,src_type_code=?2 where id= ?3 ", nativeQuery = true)
    int saveTaskInfo(String domainCode,String srcTypeCode,int id);

    //设置任务的完成状态
    @Modifying
    @Query(value = "update task_info set state=?1 where id= (select task_id from template where group_id=?2) ", nativeQuery = true)
    int setTaskState(int state,int groupId);

    //设置表格类的任务完成状态
    @Modifying
    @Query(value = "update task_info set state=?1 where id= (select task_id from table_detail_config where id=?2) ", nativeQuery = true)
    int setTableTaskState(int state,int id);

    //根据taskId设置任务的完成状态
    @Modifying
    @Query(value = "update task_info set state=1 where id=?1) ", nativeQuery = true)
    int setTaskStateById(int taskId);

    //根据templateId查询task的url
    @Query(value = "select section_url from task_info where id =(select task_id from template where id=?1)", nativeQuery = true)
    String selectUrlByTemplateId(int templateId);

    //根据taskId查询task
    @Query(value = "select * from task_info where id =?1", nativeQuery = true)
    TaskInfoEntity queryTaskById(int taskId);

    //根据taskId查询projectId
    @Query(value = "select project_id from task_info where id =?1", nativeQuery = true)
    String queryProjectIdByTaskId(int taskId);

    //记录任务完成时的时间
    @Modifying
    @Query(value = "update task_info set state_update_time=?1 where id=(select task_id from template where group_id=?2)", nativeQuery = true)
    int setStateUpdateTime(Timestamp t,int group_id);

    List<TaskInfoEntity> findByState(int status);


}
