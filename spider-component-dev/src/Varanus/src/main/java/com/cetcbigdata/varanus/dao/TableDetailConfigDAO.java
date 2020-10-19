package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.TableDetailConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:16
 */
@Repository
public interface TableDetailConfigDAO extends JpaRepository<TableDetailConfigEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM table_detail_config WHERE id = ?1", nativeQuery = true)
    int tableDelete(int id);

    @Query(value = "select task_id FROM table_detail_config WHERE id =?1", nativeQuery = true)
    int queryTaskIdBy(int id);


    @Query(value = "select * FROM table_detail_config WHERE task_id =?1", nativeQuery = true)
    TableDetailConfigEntity findTableByTaskId(int taskId);

}
