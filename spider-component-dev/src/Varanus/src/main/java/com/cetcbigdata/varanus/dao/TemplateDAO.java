package com.cetcbigdata.varanus.dao;


import com.cetcbigdata.varanus.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


/**
 * @author sunjunjie
 * @date 2020/8/17 17:32
 *
 *
 */

@Repository
public interface TemplateDAO extends JpaRepository<TemplateEntity, Integer> {

    //启动暂停板块
    @Modifying
    @Query(value = "update template set is_run=?2,run_time=?1 where id=?3", nativeQuery = true)
    int templateRun(Timestamp runTime, int isRun, int templateId);

    @Modifying
    @Query(value = "DELETE FROM config_list WHERE template_id = ?1", nativeQuery = true)
    int templateListDelete(int groupId);

    @Query(value = "select group_id from template where id=?1 ", nativeQuery = true)
    int queryGroupIdBytempId (int templateId);


    int countByGroupId(int groupId);

    @Query(value = "select task_id from template where id=?1 ", nativeQuery = true)
    int queryTaskIdBytempId (int templateId);


    @Modifying
    @Query(value = "update template set group_id=?1 where id=?1 ", nativeQuery = true)
    int updateGroupId (int templateId);

    @Modifying
    @Query(value = "update template set group_id=null where id=?1 ", nativeQuery = true)
    int deleteGroupId (int templateId);

    @Modifying
    @Query(value = "update template set task_id=?2,list_template_url=?3,doc_number=?4,list_page_number=?5,is_run=?6 where id=?1 ", nativeQuery = true)
    int templateBasicSave(int id,int taskId,String templateUrl,int docNumber,int pageNumber,int isRun);

    @Modifying
    @Query(value = "INSERT INTO template (task_id,list_template_url,doc_number,list_page_number,is_run) VALUES (?1,?2,?3,?4,?5) ", nativeQuery = true)
    int addTemplateBasic(int taskId,String templateUrl,int docNumber,int pageNumber,int isRun);

    @Modifying
    @Query(value = "update template set group_id=?1 where id=?2 ", nativeQuery = true)
    int setGroupId (int groupId,int templateId);

    @Query(value = "select list_template_url from template where id=?1 ", nativeQuery = true)
    String queryListTemplateUrlByTempId (int templateId);

    @Query(value = "select web_name from task_info where id=(select task_id from template where id=?1) ", nativeQuery = true)
    String queryWebNameByTempId (int templateId);


    @Modifying
    @Query(value = "update template set is_correct=?2 where id=?1 ", nativeQuery = true)
    int templateIsCorrect(int templateId,int isCorrect);

    List<TemplateEntity> findByIsCorrectAndIsRunAndTaskId(Integer isCorrect,Integer isRun,Integer taskId);


    @Modifying
    @Query(value = "DELETE FROM template  where id=?1 ", nativeQuery = true)
    int templateDelete(int templateId);

    @Query(value = "select id from config_list where template_id=?1 ", nativeQuery = true)
    int queryListIdByTempId (int templateId);

    //TemplateEntity findById(Integer templateId);

}
