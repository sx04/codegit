package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.common.RedisBloomFilter;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.ListDetailDAO;
import com.cetcbigdata.varanus.dao.OfficialDocumentDAO;
import com.cetcbigdata.varanus.dao.TaskBasicInfoDAO;
import com.cetcbigdata.varanus.entity.ListCrawlerdata;
import com.cetcbigdata.varanus.entity.ListCrawlerdataCount;
import com.cetcbigdata.varanus.entity.OfficialDetailModel;
import com.cetcbigdata.varanus.entity.OfficialQuery;
import com.cetcbigdata.varanus.entity.TaskBasicInfo;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Created with IDEA author:Matthew Date:2019-4-22 Time:13:40
 */
@Service
public class OfficialDocumentService {
	@Autowired
	private OfficialDocumentDAO officialDocumentDAO;
	@Autowired
	private ListDetailDAO listDetailDAO;
	@Autowired
	private TaskBasicInfoDAO taskBasicInfoDAO;
	@Autowired
	private RedisBloomFilter redisBloomFilter;
	@PersistenceContext
	EntityManager em;

	public Page queryListofficial(Pageable pageable) {
		Page<ListCrawlerdata> siteOffice = officialDocumentDAO.queryListofficial(pageable);
		return siteOffice;
	}


	public Page<Object> queryListCrawlerdata(Pageable pageable, OfficialQuery officialQuery) {
		StringBuilder sql = new StringBuilder(
				"select o.key_id,o.department,o.title,o.pub_office_info,o.pub_date_info,o.policy_date,o.insert_date from task_basic_info t, official_document o where t.task_id = o.task_id ");

		if (officialQuery.getIsNormal() != null) {
			sql.append(" and o.is_normal=").append(officialQuery.getIsNormal());

		}
		if (StringUtils.isNoneBlank(officialQuery.getResponsiblePeople())) {
			sql.append("  and t.responsible_people= '").append(officialQuery.getResponsiblePeople()).append("'");

		}
		if (StringUtils.isNoneBlank(officialQuery.getDepartment())) {
			sql.append("  and t.department= '").append(officialQuery.getDepartment()).append("'");

		}

		if (StringUtils.isNoneBlank(officialQuery.getSectionTitle())) {
			sql.append("  and t.section_title= '").append(officialQuery.getSectionTitle()).append("'");

		}
		// SqlPageUtil sqlPageUtil = new SqlPageUtil();
		return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, ListCrawlerdata.class);
	}

	public Page<Object> queryListCrawlerdata4(Pageable pageable, OfficialQuery officialQuery) {
		StringBuilder sql = new StringBuilder(
				"SELECT t.responsible_people, t.task_id, t.department, t.section_title, t.section_url, l.list_page_number, " +
						"l.page_url_count, l.list_xpath, l.list_client_type, l.list_request_type, l.list_request_params, " +
						"l.list_page_header, l.list_need_proxy, l.list_response_type, l.list_json_field, l.list_page_name, " +
						"l.list_json_key, l.list_json_substring, l.list_id_url, l.json_id_key, l.doc_host, o.section_doc_count  " +
						"FROM task_basic_info t, list_detail l, ( SELECT task_id, is_normal, count( 0 ) section_doc_count FROM official_document GROUP BY task_id, is_normal ) o  " +
						"WHERE t.task_id = l.task_id  AND l.task_id = o.task_id");
		if (officialQuery.getIsNormal() != null) {
			sql.append(" and o.is_normal=").append(officialQuery.getIsNormal());
		}
		if (StringUtils.isNoneBlank(officialQuery.getResponsiblePeople())) {
			sql.append("  and t.responsible_people= '").append(officialQuery.getResponsiblePeople()).append("'");
		}
		if (StringUtils.isNoneBlank(officialQuery.getDepartment())) {
			sql.append("  and t.department= '").append(officialQuery.getDepartment()).append("'");
		}
		if (StringUtils.isNoneBlank(officialQuery.getSectionTitle())) {
			sql.append("  and t.section_title= '").append(officialQuery.getSectionTitle()).append("'");
		}
		if (StringUtils.isNoneBlank(officialQuery.getTaskId()) && officialQuery.getTaskId() != null) {
			sql.append(" and t.task_id=").append(officialQuery.getTaskId());
		}
		sql.append(" ORDER BY t.task_id,t.responsible_people");
			return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, ListCrawlerdataCount.class);
	}

//	public Page<Object> queryByConditionNQ(String sql, Pageable pageRequest, Class class1) {
//		Query q = em.createNativeQuery(sql, class1);
//		String countSql = "select count(*) from (" + sql + ") count_sql";
//		Query countQ = em.createNativeQuery(countSql);
//		Assert.notNull(q);
//		Assert.notNull(countQ);
//		List<Object> totals = countQ.getResultList();
//		Long total = 0L;
//		for (Object elementObj : totals) {
//			BigInteger elementBi = (BigInteger) elementObj;// 转换成大类型
//			Long element = elementBi.longValue(); // 转换成long类型
//			total += element == null ? 0 : element;
//		}
//
//		q.setFirstResult(pageRequest.getOffset());
//		q.setMaxResults(pageRequest.getPageSize());
//		List<Object> content = total > pageRequest.getOffset() ? q.getResultList() : Collections.<Object>emptyList();
//		return new PageImpl<Object>(content, pageRequest, total);
//	}

//按条件查询详情列表
		public Page<Object> queryListOfficialByofficial(Pageable pageable, OfficialQuery officialQuery) {
			StringBuilder sql = new StringBuilder(
					"select d.*,t.sql_table,t.department,t.area,t.section_title,t.section_url," +
							"t.is_valid,t.insert_time, t.update_date,t.url_availability, t.responsible_people," +
							"t.site_version,t.version_exception_count from doc_detail d,task_basic_info t " +
							"WHERE d.task_id=t.task_id ");
			if(Optional.ofNullable(officialQuery).isPresent()){

				String taskId = String.valueOf(officialQuery.getTaskId());
				if (StringUtils.isNoneBlank(taskId) && !taskId.equals("null")) {
					sql.append(" and t.task_id=").append(taskId);
				}

				if (StringUtils.isNoneBlank(officialQuery.getResponsiblePeople())) {
					sql.append("  and t.responsible_people= '").append(officialQuery.getResponsiblePeople()).append("'");

				}
				if (StringUtils.isNoneBlank(officialQuery.getDepartment())) {
					sql.append("  and t.department= '").append(officialQuery.getDepartment()).append("'");

				}

				if (StringUtils.isNoneBlank(officialQuery.getSectionTitle())) {
					sql.append("  and t.section_title= '").append(officialQuery.getSectionTitle()).append("'");

				}
			}

			sql.append(" ORDER BY d.doc_id DESC ");

			// SqlPageUtil sqlPageUtil = new SqlPageUtil();
			return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em,OfficialDetailModel.class);
		}

		@Transactional
		public void dataDelete (int taskId,int listId){
			TaskBasicInfo taskBasicInfo = taskBasicInfoDAO.findByTaskId(taskId);
			if (Optional.ofNullable(taskBasicInfo).isPresent()) {
				officialDocumentDAO.deleteOfficialDocumentByTaskIdAndListId(taskId,listId);
				listDetailDAO.deleteListDetail(listId);
				String bloomFilterKey = PageParserHelper.assebleBloomFilter(taskBasicInfo.getDepartment(),
						taskBasicInfo.getSectionTitle());
				redisBloomFilter.delete(bloomFilterKey);
			}
		}
	}
