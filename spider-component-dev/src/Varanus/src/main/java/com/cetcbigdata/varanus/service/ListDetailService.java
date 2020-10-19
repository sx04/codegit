package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.entity.OfficialQuery;
import com.cetcbigdata.varanus.entity.SiteEntity;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Created with IDEA author:Matthew Date:2019-4-22 Time:13:39
 */
@Service
public class ListDetailService {

	@PersistenceContext
	EntityManager em;
//按条件查询信息列表
	public Page<Object> queryListDetailByCondition(Pageable pageable, OfficialQuery officialQuery) {


		StringBuilder sql = new StringBuilder(
				"SELECT t.task_id,t.department,t.area,t.section_title,t.section_url,t.is_valid,t.update_date,t.site_version,t.responsible_people, "
						+ "l.list_id,l.list_template_url,l.list_page_number,l.list_xpath,l.list_client_type,l.list_request_type, "
						+ "l.list_request_params,l.list_need_proxy,l.list_response_type,l.list_json_field,l.list_repeat, "
						+ "l.list_page_name,l.list_json_key,l.list_json_substring,l.list_id_url,l.json_id_key,"
						+ "l.doc_host,l.list_year_page,l.list_page_header,l.list_driver_page_js,t.domain_name,t.data_src_type_name,l.page_url_count,l.list_href,l.doc_href "
						+ "FROM list_detail l, task_basic_info t WHERE l.task_id = t.task_id ");

		if(Optional.ofNullable(officialQuery).isPresent()){

			if(StringUtils.isNoneBlank(officialQuery.getIsValid())){
				sql.append(" and t.is_valid=").append(officialQuery.getIsValid());

			}

			if (StringUtils.isNoneBlank(officialQuery.getTaskId())) {
				sql.append(" and t.task_id=").append(officialQuery.getTaskId());
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
		sql.append(" order by l.list_id desc");

		return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, SiteEntity.class);
	}
}
