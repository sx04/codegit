package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.ListDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Deprecated
@Repository
public interface ListDetailDAO extends JpaRepository<ListDetail, Integer> {

	ListDetail findByTaskId(int taskId);

	@Transactional
	@Modifying
	@Query(value = "update list_detail u set u.last_crawler_url = ?1 where u.list_id = ?2", nativeQuery = true)
	int setLastCrawlerUrlFor(String lastCrawlerUrl, Integer listId);

//	@Query(value = "SELECT new com.cetcbigdata.varanus.entity.SiteEntity(l, t) FROM ListDetail l, TaskBasicInfo t WHERE l.taskId = t.taskId order by t.updateDate desc ")
//	List<SiteEntity> queryListDetails();

	@Transactional
	@Modifying
	@Query(value = "delete from list_detail where task_id =?1", nativeQuery = true)
	int deleteTask(Integer taskId);

	@Modifying
	@Query(value = "update list_detail u set u.last_crawler_url = NULL where u.list_id = ?1", nativeQuery = true)
	int deleteListDetail(Integer listId);

	@Query(value = "SELECT \n" +
			"t.task_id,\n" +
			"t.department,\n" +
			"t.area,\n" +
			"t.section_title,\n" +
			"t.section_url,\n" +
			"t.is_valid,\n" +
			"t.site_version,\n" +
			"t.responsible_people, \n" +
			"l.list_id,\n" +
			"l.list_template_url,\n" +
			"l.list_page_number,\n" +
			"l.list_xpath,\n" +
			"l.list_client_type,\n" +
			"l.list_request_type, \n" +
			"l.list_request_params,\n" +
			"l.list_need_proxy,\n" +
			"l.list_response_type,\n" +
			"l.list_json_field,\n" +
			"l.list_repeat, \n" +
			"l.list_page_name,\n" +
			"l.list_json_key,\n" +
			"l.list_json_substring,\n" +
			"l.list_id_url,\n" +
			"l.json_id_key,\n" +
			"l.doc_host,\n" +
			"l.list_year_page,\n" +
			"l.list_page_header,\n" +
			"l.list_driver_page_js\n" +
			"FROM list_detail l, task_basic_info t \n" +
			"WHERE l.task_id = t.task_id and t.task_id=?1", nativeQuery = true)
	List queryOneList(Integer taskId);
}
