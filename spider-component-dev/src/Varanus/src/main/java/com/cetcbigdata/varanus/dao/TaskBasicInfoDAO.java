package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.TaskBasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Deprecated
@Repository
public interface TaskBasicInfoDAO extends JpaRepository<TaskBasicInfo, Integer> {

	TaskBasicInfo findByTaskId(int taskId);

	@Query(value = "select is_valid from task_basic_info where task_id =?1", nativeQuery = true)
	Boolean findTaskBasicInfoByTaskId(Integer taskId);

	// @Query(value = "select
	// task_id,sql_table,department,area,section_title,section_url "
	// + "from task_basic_info where is_valid = 1", nativeQuery = true)
	@Query(value = "select * from task_basic_info where is_valid = 1 ORDER BY task_id DESC", nativeQuery = true)
	List<TaskBasicInfo> findTasks();

	@Transactional
	@Modifying
	@Query(value = "update task_basic_info u set u.is_valid = ?1 where u.task_id = ?2", nativeQuery = true)
	int updateTaskStatus(Integer isValid, Integer taskId);

	//向前台返回人名
	@Query(value = "select responsible_people from task_basic_info group by responsible_people",nativeQuery = true)
	List<String> findAllByResponsiblePeoples();

	//根据部门名向前台返回模块名
	@Query(value = "select distinct `section_title` from task_basic_info where department =?1\n", nativeQuery = true)
	List<String> findSectiontitle(String department);

	//根据模糊查询向前台返回部门名
	@Query(value = "select distinct `department` from task_basic_info where department like %?1%\n", nativeQuery = true)
	List<String> findDepartment(String department);

	@Query(value = "select department from task_basic_info group by department",nativeQuery = true)
	List<String> findAllBydepartments();

	@Query(value = "select task_id from task_basic_info group by task_id",nativeQuery = true)
	List<String> findAllByTaskIds();

	@Query(value = "select domain_name from list_domain",nativeQuery = true)
	List<String> findDomains();

	@Query(value = "select data_src_type_name from list_datasrctype",nativeQuery = true)
	List<String> findDataSrcTypeName();
}
