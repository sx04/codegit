package com.cetcbigdata.varanus.utils;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

public class SqlPageUtil {

	public static Page<Object> queryByConditionNQ(String sql, Pageable pageRequest, EntityManager em, Class classType) {
		Query q = em.createNativeQuery(sql, classType);
		String countSql = "select count(*) from (" + sql + ") count_sql";
		Query countQ = em.createNativeQuery(countSql);
		Assert.notNull(q);
		Assert.notNull(countQ);
		List<Object> totals = countQ.getResultList();
		Long total = 0L;
		for (Object elementObj : totals) {
			BigInteger elementBi = (BigInteger) elementObj;// 转换成大类型
			Long element = elementBi.longValue(); // 转换成long类型
			total += element == null ? 0 : element;
		}

		q.setFirstResult(pageRequest.getOffset());
		q.setMaxResults(pageRequest.getPageSize());
		List<Object> content = total > pageRequest.getOffset() ? q.getResultList() : Collections.<Object>emptyList();
		return new PageImpl<Object>(content, pageRequest, total);
	}
}
