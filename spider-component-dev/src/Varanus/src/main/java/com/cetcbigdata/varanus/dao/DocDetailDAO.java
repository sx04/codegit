package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.DocDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Deprecated
@Repository
public interface DocDetailDAO extends JpaRepository<DocDetail, Integer> {

	@Query(value = "select * from doc_detail where list_id = ?1", nativeQuery = true)
	List<DocDetail> findByListId(int listId);

	@Query(value = "select * from doc_detail where task_id = ?1", nativeQuery = true)
	List<DocDetail> findByTaskId(int taskId);

	@Query(value = "select * from doc_detail", nativeQuery = true)
	List<DocDetail> findDocDetails();

	@Transactional
	@Modifying
	@Query(value = "delete from doc_detail where task_id =?1",nativeQuery = true)
	int deleteTask(Integer taskId);

	DocDetail findDocDetailByDocId(int docId);
}
