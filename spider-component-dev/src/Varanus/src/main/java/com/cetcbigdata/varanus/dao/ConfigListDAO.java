package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.ConfigListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author sunjunjie
 * @date 2020/8/18 15:16
 */
public interface ConfigListDAO extends JpaRepository<ConfigListEntity, Integer> {

    @Query(value = "select project_id from task_info where id =(select task_id from template where id=(select template_id from config_list where id=?1))", nativeQuery = true)
    String findProjectIdByListId (int li);

    @Query(value = "select project_id from task_info where id =(select task_id from template where id=?1)", nativeQuery = true)
    String findProjectIdByTemplateId (int ti);

    @Query(value = "select group_id from template where id =?1", nativeQuery = true)
    int findGroupIdByTemplateId (int i);

    @Query(value = "select web_name from task_info where id=(select task_id from template where id=(select template_id from config_list where id=?1))", nativeQuery = true)
    String findWebNameByListId (int ti);



}
