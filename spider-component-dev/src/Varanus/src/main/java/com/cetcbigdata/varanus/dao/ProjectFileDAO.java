package com.cetcbigdata.varanus.dao;


import com.cetcbigdata.varanus.entity.ProjectFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
public interface ProjectFileDAO extends JpaRepository<ProjectFileEntity, Integer> {


    //根据项目code获取信该项目字段
    @Query(value="select * from project_file where project_id=?1 order by file_sort", nativeQuery = true)
    List<ProjectFileEntity> findProjectFieldEntitiesByProjectId(String code);

    //根据id获取字段内容
    ProjectFileEntity findProjectFieldEntityById(int id);

    //项目信息页所需字段删除
    int deleteProjectFieldEntitiesById(int id);

    //项目信息页所需字段添加
    @Modifying
    @Query(value = "insert into project_field(project_id,field_code,field_name,field_sort,field_type) values(?1,?2,?3,?4,?5)", nativeQuery = true)
    int addProjectField(String projectId,String fieldCode ,String fieldName,int fieldSort,String fieldType);

    //根据表名查找表
    @Query(value="select table_name from information_schema.tables where table_name=?1", nativeQuery = true)
    String findDetailTable(String tableName);


}
