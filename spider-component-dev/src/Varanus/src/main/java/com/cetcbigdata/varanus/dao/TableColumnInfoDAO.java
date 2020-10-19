package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.TableColumnInfoEntity;
import com.cetcbigdata.varanus.entity.TableDetailConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:21
 */
@Repository
public interface TableColumnInfoDAO extends JpaRepository<TableColumnInfoEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM table_column_info WHERE task_id = ?1", nativeQuery = true)
    int columnInfoDelete(int id);


    @Query(value = "select * FROM table_column_info WHERE task_id =?1", nativeQuery = true)
    List<TableColumnInfoEntity> findColumnByTaskId(int taskId);

    @Query(value = "select task_id FROM table_column_info WHERE id =?1", nativeQuery = true)
    int findTaskIdById(int id);

}
