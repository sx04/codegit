package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.TaskWarning;
import com.cetcbigdata.varanus.entity.TaskWarningEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskWarningDAO extends JpaRepository<TaskWarningEntity, Integer> {

	@Query(value = "SELECT task_id, COUNT( list_warning_type = 0 OR NULL ) AS list_network_count, "
			+ "COUNT( list_warning_type = 1 OR NULL ) AS list_url_count, "
			+ "COUNT( list_warning_type = 2 OR NULL ) AS list_template_count, "
			+ "COUNT( doc_warning_type = 0 OR NULL ) AS doc_network_count, "
			+ "COUNT( doc_warning_type = 1 OR NULL ) AS doc_url_count, "
			+ "COUNT( doc_warning_type = 2 OR NULL ) AS doc_template_count "
			+ "FROM task_warning WHERE TO_DAYS( warning_time ) = TO_DAYS(?1) GROUP BY task_id", nativeQuery = true)
	Object[] findDailyWarning(String warningDate);

	@Query(value = "select id,task_id,list_warning_type,list_warning_detail,doc_warning_type,doc_warning_detail,warning_time "
			+ "FROM task_warning WHERE task_id = ?1 AND TO_DAYS( warning_time ) = TO_DAYS(?2) ORDER BY id DESC", nativeQuery = true)
	List<TaskWarning> findDailyWarningByTaskId(int taskId, String warningDate);


}
