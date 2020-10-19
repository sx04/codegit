package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.dao.ProjectinfoDAO;
import com.cetcbigdata.varanus.entity.TaskWarningEntity;
import com.cetcbigdata.varanus.entity.TaskWarningListEntity;
import com.cetcbigdata.varanus.entity.WarningListEntity;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author sunjunjie
 * @date 2020/9/1 9:24
 */
@Service
public class WarningService {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ProjectinfoDAO projectinfoDAO;

   //查询单条模板下的报警信息详情
    public Page<Object> queryWarningOneList(Pageable pageable, String templateId,String data) {

        StringBuilder sql = new StringBuilder("select * FROM task_warning WHERE template_id =");
        sql.append(templateId);
        if (StringUtils.isNoneBlank(data)) {
            sql.append(" and TO_DAYS(warning_time) = TO_DAYS('").append(data).append("')");
        }
        String s = sql.append(" ORDER BY warning_time").toString();
        Query q = em.createNativeQuery(s, TaskWarningEntity.class);
        String countSql = "select count(*) from (" + s + ") count_sql";
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
        q.setFirstResult(pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());

        List<Object>  warningList = q.getResultList();
        for (Object warning : warningList) {
            TaskWarningListEntity warning1 = (TaskWarningListEntity)warning;
            if (warning1.getListWarningType() != null) {
                Integer listWarningType = Integer.parseInt(warning1.getListWarningType());
                switch (listWarningType) {
                    case 0:
                        warning1.setListWarningType(Constants.NETWORK_WARNING_REASON);
                        break;
                    case 1:
                        warning1.setListWarningType(Constants.URL_WARNING_REASON);
                        break;
                    case 2:
                        warning1.setListWarningType(Constants.TASK_TEMPLATE_WARNING_REASON);
                        break;
                }
            }
            if (warning1.getDetailWarningType()!= null) {
                Integer docWarningType = Integer.parseInt(warning1.getDetailWarningType());
                switch (docWarningType) {
                    case 0:
                        warning1.setDetailWarningType(Constants.NETWORK_WARNING_REASON);
                        break;
                    case 1:
                        warning1.setDetailWarningType(Constants.URL_WARNING_REASON);
                        break;
                    case 2:
                        warning1.setDetailWarningType(Constants.TASK_TEMPLATE_WARNING_REASON);
                        break;
                }
            }
        }
        List<Object> content = total > pageable.getOffset() ? warningList : Collections.<Object>emptyList();
        return new PageImpl<Object>(content, pageable, total);
    }

    //查询多条模板报警信息概况
    public Page<Object> queryTaskWarningList(Pageable pageable,Map maps ) {

        StringBuilder sql = new StringBuilder("SELECT tw.template_id,\n" +
                "COUNT( tw.list_warning_type = 0 OR NULL ) AS list_network_count,\n" +
                "COUNT( tw.list_warning_type = 1 OR NULL ) AS list_url_count, \n" +
                "COUNT( tw.list_warning_type = 2 OR NULL ) AS list_template_count, \n" +
                "COUNT( tw.detail_warning_type = 0 OR NULL ) AS doc_network_count,\n" +
                "COUNT( tw.detail_warning_type = 1 OR NULL ) AS doc_url_count, \n" +
                "COUNT( tw.detail_warning_type = 2 OR NULL ) AS doc_template_count,\n" +
                "su.user_name,\n" +
                "tw.is_deal,\n" +
                "tw.deal_time\n" +
                "from task_warning tw \n" +
                "left join sys_user su on su.id=tw.deal_user_id \n" +
                "join template tem on tem.id=tw.template_id \n" +
                "join task_info ta on ta.id = tem.task_id where 1=1");

        if (maps.get("isDeal")!=null) {
            if (StringUtils.isNoneBlank(maps.get("isDeal").toString())) {
                sql.append(" and tw.is_deal ='").append(maps.get("isDeal").toString()).append("'");
            }
        }

        if (maps.get("warningTime")!=null) {
            if (StringUtils.isNoneBlank(maps.get("warningTime").toString())) {
                sql.append(" and TO_DAYS(warning_time) = TO_DAYS('").append(maps.get("warningTime").toString()).append("')");
            }
        }
        if (maps.get("projectName")!=null) {
            String name = maps.get("projectName").toString();
            String code = projectinfoDAO.queryProjectByName(name);
            if (StringUtils.isNoneBlank(maps.get("projectName").toString())) {
                sql.append(" and ta.project_id ='").append(code).append("'");
            }
        }
        sql.append(" GROUP BY template_id");
        return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, WarningListEntity.class);
    }

    //查询单人的多条模板报警信息概况
    public Object queryUserWarningList(int userId) {

        StringBuilder sql = new StringBuilder("SELECT tw.template_id,su.user_name,\n" +
                "COUNT( tw.list_warning_type = 0 OR NULL ) AS list_network_count,\n" +
                "COUNT( tw.list_warning_type = 1 OR NULL ) AS list_url_count,\n" +
                "COUNT( tw.list_warning_type = 2 OR NULL ) AS list_template_count,\n" +
                "COUNT( tw.detail_warning_type = 0 OR NULL ) AS doc_network_count,\n" +
                "COUNT( tw.detail_warning_type = 1 OR NULL ) AS doc_url_count,\n" +
                "COUNT( tw.detail_warning_type = 2 OR NULL ) AS doc_template_count\n" +
                "from task_warning tw \n" +
                "join template tem on tem.id=tw.template_id \n" +
                "join task_info ta on ta.id = tem.task_id\n" +
                "join sys_user su on su.id=ta.get_user_id");
        if(userId>0){
            sql.append(" where ta.get_user_id=").append(userId);
        }
        sql.append(" GROUP BY template_id\n" +
                "ORDER BY tw.warning_time DESC\n" +
                "limit 10");
        String s = sql.toString();
        Query query = em.createNativeQuery(s);
        List list = query.getResultList();
        List<WarningListEntity> warningListEntities = new ArrayList<>();
        try {
            for (int i = 0; i < list.size(); i++) {
                WarningListEntity warningListEntity = new WarningListEntity();
                Object[] obj = (Object[]) list.get(i);
                warningListEntity.setTemplateId(Integer.parseInt(obj[0].toString()));
                warningListEntity.setUserName(obj[1].toString());
                warningListEntity.setListNetworkCount(obj[2].toString());
                warningListEntity.setListUrlCount(obj[3].toString());
                warningListEntity.setListTemplateCount(obj[4].toString());
                warningListEntity.setDocNetworkCount(obj[5].toString());
                warningListEntity.setDocUrlCount(obj[6].toString());
                warningListEntity.setDocTemplateCount(obj[7].toString());
                warningListEntities.add(warningListEntity);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return warningListEntities;
    }

    @Transactional
    public int updateWarningState(int templateId,int userId){
        Timestamp creatTime = new Timestamp(new Date().getTime());
        StringBuilder sql = new StringBuilder("update task_warning set is_deal=1,deal_user_id= ");
        sql.append(userId).append(" ,deal_time= '").append(creatTime).append("' where template_id=").append(templateId);
        String s = sql.toString();
        Query query = em.createNativeQuery(s);
        int i = query.executeUpdate();
        return i;
    }

}
