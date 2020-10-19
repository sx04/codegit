package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.DocIncrement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Deprecated
@Repository
public interface DocIncrementDAO extends JpaRepository<DocIncrement, Integer> {

	@Query(value = "SELECT department,section,SUM( increment ) FROM doc_increment "
			+ "WHERE TO_DAYS( insert_time ) = TO_DAYS( NOW( ) ) GROUP BY department,section", nativeQuery = true)
	Object[] findIncrementGroupByDeparnment();
}
