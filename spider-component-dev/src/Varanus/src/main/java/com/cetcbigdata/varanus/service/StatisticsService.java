package com.cetcbigdata.varanus.service;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.dao.ProjectinfoDAO;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.utils.HumpToLine;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunjunjie
 * @date 2020/8/31 15:07
 */
@Service
public class StatisticsService {

    @PersistenceContext
    EntityManager entityManger;

    @Autowired
    private ProjectinfoDAO projectinfoDAO;

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

    public Page<Object> queryDataList(Pageable pageable, Map maps) {

        String name = maps.get("projectName").toString();
        String code = projectinfoDAO.queryProjectByName(name);
        StringBuilder tableDataName = new StringBuilder("detail_" + code + "_data");
        StringBuilder sql = new StringBuilder("select d.id,t.web_name,t.section_title,d.title,d.insert_date from ");
        sql.append(tableDataName).append(" d join task_info t on t.id=" +
                "(select tm.task_id from template tm where tm.id=d.template_id) where 1=1");

        if (maps.get("taskId") != null) {
            if (StringUtils.isNoneBlank(maps.get("taskId").toString())) {
                sql.append(" and t.id ='").append(maps.get("taskId").toString()).append("'");
            }
        }
        if (maps.get("userName") != null) {
            if (StringUtils.isNoneBlank(maps.get("userName").toString())) {
                sql.append(" and t.get_user_id ='").append(maps.get("userName").toString()).append("'");
            }
        }
        if (maps.get("date") != null) {
            if (StringUtils.isNoneBlank(maps.get("date").toString())) {
                sql.append(" and d.insert_date >='").append(maps.get("date").toString()).append(" 00:00:00'");
                sql.append(" and d.insert_date <='").append(maps.get("date").toString()).append(" 23:59:59'");
            }
        }
        sql.append(" ORDER BY insert_date");

        return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, entityManger, StatisticsEntity.class);
    }

    public Map queryDataOne(String id, String projectName) {

        String code = projectinfoDAO.queryProjectByName(projectName);
        StringBuilder tableDataName = new StringBuilder("detail_" + code + "_data");
        StringBuilder sql = new StringBuilder("select * from ");
        sql.append(tableDataName);
        if (StringUtils.isNoneBlank(id)) {
            sql.append(" where id ='").append(id).append("'");
        }
        String s = sql.toString();
        Query query = entityManger.createNativeQuery(s);
        //行值
        Object obj = query.getSingleResult();
        List list1 = JSON.parseArray(JSON.toJSONString(obj), String.class);
        StringBuilder selectName = new StringBuilder("select column_name from information_schema.columns where table_name='");
        String s1 = selectName.append(tableDataName).append("'").toString();
        Query query1 = entityManger.createNativeQuery(s1);
        //列名
        List list2 = query1.getResultList();
        Map map = new HashMap();
        for (int i = 0; i < list2.size(); i++) {
            String key = HumpToLine.lineToHump(list2.get(i).toString());
            map.put(key, list1.get(i));
        }
        return map;
    }

    public Object projectTaskNum() {
        List<Object[]> projectNameCode = projectinfoDAO.queryProjectCodeName();
        List taskTotal = projectinfoDAO.projectTaskTotal();
        List taskGet = projectinfoDAO.projectTaskGet();
        List taskFinish = projectinfoDAO.projectTaskFinish();
        List taskNnfinish = projectinfoDAO.projectTaskUnfinish();
        Map<String, ProjectTaskNumEntity> taskMap = new HashMap();

        for (int i = 0; i < projectNameCode.size(); i++) {
            ProjectTaskNumEntity projectTaskNumEntity = new ProjectTaskNumEntity(0, 0, 0, 0);
            Object[] obj = projectNameCode.get(i);
            String code = obj[0].toString();
            String name = obj[1].toString();
            projectTaskNumEntity.setProjectName(name);
            taskMap.put(code, projectTaskNumEntity);
        }

        for (int i = 0; i < taskTotal.size(); i++) {
            Object[] obj = (Object[]) taskTotal.get(i);
            String code = obj[0].toString();//项目名
            int num = Integer.parseInt(obj[1].toString());//数量
            ProjectTaskNumEntity projectTaskNumEntity = taskMap.get(code);
            projectTaskNumEntity.setTaskTotal(num);
        }

        for (int i = 0; i < taskGet.size(); i++) {
            Object[] obj = (Object[]) taskGet.get(i);
            String code = obj[0].toString();//项目名
            int num = Integer.parseInt(obj[1].toString());//数量
            ProjectTaskNumEntity projectTaskNumEntity = taskMap.get(code);
            projectTaskNumEntity.setTaskGet(num);
        }

        for (int i = 0; i < taskFinish.size(); i++) {
            Object[] obj = (Object[]) taskFinish.get(i);
            String code = obj[0].toString();//项目名
            int num = Integer.parseInt(obj[1].toString());//数量
            ProjectTaskNumEntity projectTaskNumEntity = taskMap.get(code);
            projectTaskNumEntity.setTaskFinish(num);
        }

        for (int i = 0; i < taskNnfinish.size(); i++) {
            Object[] obj = (Object[]) taskNnfinish.get(i);
            String code = obj[0].toString();//项目名
            int num = Integer.parseInt(obj[1].toString());//数量
            ProjectTaskNumEntity projectTaskNumEntity = taskMap.get(code);
            projectTaskNumEntity.setTaskUnfinish(num);
        }
        return taskMap;
    }

    public Object projectCountNum(int userId) {
        StringBuilder sql1 = new StringBuilder("select pi.code,pi.name from project_info pi join project_user pu on pi.code=pu.project_id where pu.user_id=");
        String selectName = sql1.append(userId).toString();
        Query query1 = entityManger.createNativeQuery(selectName);
        List<Object[]> list = query1.getResultList();
        List<ProjectCountNum> pc = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String code = list.get(i)[0].toString();
            String name = list.get(i)[1].toString();
            ProjectCountNum projectCountNum = new ProjectCountNum();
            projectCountNum.setCode(code);
            projectCountNum.setName(name);
            pc.add(projectCountNum);
        }

        for (int i = 0; i < pc.size(); i++) {
            String projectId = pc.get(i).getCode();
            StringBuilder selectDetail = new StringBuilder("SHOW TABLES LIKE 'detail_");
            selectDetail.append(projectId).append("_data'");
            String selectDetailsql = selectDetail.toString();
            Query query3 = entityManger.createNativeQuery(selectDetailsql);
            try {
                query3.getSingleResult();
                StringBuilder sql2 = new StringBuilder("select count(1) as dataNum from detail_");
                sql2.append(projectId).append("_data");
                String selectDataNum = sql2.toString();
                Query query2 = entityManger.createNativeQuery(selectDataNum);
                int dataNum = Integer.parseInt(query2.getSingleResult().toString());
                pc.get(i).setDataNum(dataNum);
            } catch (Exception e) {
                LOG.error("该项目还没创建建数据表");
            }
        }

        for (int i = 0; i < pc.size(); i++) {
            String projectId = pc.get(i).getCode();
            int tempNum = projectinfoDAO.tempNumByprojectId(projectId);
            pc.get(i).setTempNum(tempNum);
        }

        for (int i = 0; i < pc.size(); i++) {
            String projectId = pc.get(i).getCode();
            int warningNum = projectinfoDAO.warningNumByprojectId(projectId);
            pc.get(i).setWarningNum(warningNum);
        }

        for (int i = 0; i < pc.size(); i++) {
            String projectId = pc.get(i).getCode();
            int unGet = projectinfoDAO.unGetByprojectId(projectId);
            pc.get(i).setUnGet(unGet);
        }
        return pc;
    }

    public Object queryAdminTemplateCount() {
        StringBuilder sql = new StringBuilder("select pi.name,ti.web_name,ti.section_title,su.user_name,ti.state_update_time \n" +
                "from task_info ti join project_info pi on ti.project_id=pi.code join sys_user su on su.id=ti.get_user_id \n" +
                "ORDER BY ti.state_update_time DESC limit 10");
        String s = sql.toString();
        Query query = entityManger.createNativeQuery(s);
        List<Object[]> list = query.getResultList();
        List<AdminTemplateEntity> pc = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            AdminTemplateEntity adminTemplateEntity = new AdminTemplateEntity();
            String name = list.get(i)[0].toString();
            String webName = list.get(i)[1].toString();
            String sectionTitle = list.get(i)[2].toString();
            String userName = list.get(i)[3].toString();
            if (list.get(i)[4] != null) {
                String stateUpdateTime = list.get(i)[4].toString();
                adminTemplateEntity.setStateUpdateTime(stateUpdateTime);
            }
            adminTemplateEntity.setName(name);
            adminTemplateEntity.setWebName(webName);
            adminTemplateEntity.setSectionTitle(sectionTitle);
            adminTemplateEntity.setUserName(userName);
            pc.add(adminTemplateEntity);
        }
        return pc;
    }

    public Map<String, ProjectNumCount> queryDataTemplateCountById() {

        Map<String, ProjectNumCount> projectMap = new HashMap();
        //查询项目下当日新增模板数量
        StringBuilder sql1 = new StringBuilder("select pi.code,count(*) from project_info pi join task_info ti on ti.project_id=pi.code join template tm on tm.task_id=ti.id \n" +
                "where to_days(ti.state_update_time) = to_days(now())\n" +
                "group by project_id");
        //查询项目下总共模板数量
        StringBuilder sql2 = new StringBuilder("select pi.code,count(*) from project_info pi join task_info ti on ti.project_id=pi.code join template tm on tm.task_id=ti.id \n" +
                "group by project_id");

        Query query1 = entityManger.createNativeQuery(sql1.toString());
        List<Object[]> list1 = query1.getResultList();

        Query query2 = entityManger.createNativeQuery(sql2.toString());
        List<Object[]> list2 = query2.getResultList();

        List<Object[]> list = projectinfoDAO.queryProjectCodeName();

        for (int i = 0; i < list.size(); i++) {
            String code = list.get(i)[0].toString();
            String name = list.get(i)[1].toString();
            ProjectNumCount projectNumCount = new ProjectNumCount();
            projectNumCount.setProjectId(code);
            projectNumCount.setProjectName(name);
            projectMap.put(code, projectNumCount);
        }

        //将各项目下今日模板数存储入map集合里的projectNumCount对象中
        for (int i = 0; i < list1.size(); i++) {
            String code = list1.get(i)[0].toString();
            int count = Integer.parseInt(list1.get(i)[1].toString());
            ProjectNumCount projectNumCount = projectMap.get(code);
            projectNumCount.setTodayTempAdd(count);
        }
        //将各项目下总模板数存储入map集合里的projectNumCount对象中
        for (int i = 0; i < list2.size(); i++) {
            String code = list2.get(i)[0].toString();
            int count = Integer.parseInt(list2.get(i)[1].toString());
            ProjectNumCount projectNumCount = projectMap.get(code);
            projectNumCount.setTempNum(count);
        }

        //将各项目下采集数据总量存储入map集合里的projectNumCount对象中
        for (Map.Entry<String, ProjectNumCount> m : projectMap.entrySet()) {
            String code = m.getKey();
            StringBuilder selectDetail = new StringBuilder("SHOW TABLES LIKE 'detail_");
            selectDetail.append(code).append("_data'");
            String selectDetailsql = selectDetail.toString();
            Query query = entityManger.createNativeQuery(selectDetailsql);
            try {
                query.getSingleResult();
                StringBuilder sql3 = new StringBuilder("select count(1) from detail_");
                sql3.append(code).append("_data");
                Query query3 = entityManger.createNativeQuery(sql3.toString());
                //数据总量
                int dataNum = Integer.parseInt(query3.getSingleResult().toString());
                m.getValue().setDataNum(dataNum);
                sql3.append(" where to_days(insert_date) = to_days(now())");
                Query query4 = entityManger.createNativeQuery(sql3.toString());
                //今日新增数据量
                int todayDataAdd = Integer.parseInt(query4.getSingleResult().toString());
                m.getValue().setTodayDataAdd(todayDataAdd);
            } catch (Exception e) {
                LOG.error("该项目还没创建建数据表");
            }
        }
        return projectMap;
    }

    public Object queryAdminDataTemplateByDate(String startDate, String endDate, String code) {
        //时间段内的项目模板新增量
        StringBuilder sql5 = new StringBuilder("select count(*) from project_info pi \n" +
                "join task_info ti on ti.project_id=pi.code join template tm on tm.task_id=ti.id where pi.code='");
        sql5.append(code).append("'");
        if (StringUtils.isNoneBlank(startDate)) {
            sql5.append(" and ti.state_update_time >'").append(startDate).append("'");
        }
        if (StringUtils.isNoneBlank(endDate)) {
            sql5.append(" and ti.state_update_time <'").append(endDate).append("'");
        }
        Query query5 = entityManger.createNativeQuery(sql5.toString());
        int tempNum = Integer.parseInt(query5.getSingleResult().toString());
        ProjectNumCount projectNumCount = new ProjectNumCount();
        projectNumCount.setProjectId(code);
        projectNumCount.setTempNum(tempNum);
        //检查项目数据表是否存在
        StringBuilder sql = new StringBuilder("SHOW TABLES LIKE 'detail_");
        sql.append(code).append("_data'");
        Query query2 = entityManger.createNativeQuery(sql.toString());
        try {
            query2.getSingleResult();
            //时间段内的项目数据新增量
            StringBuilder sql4 = new StringBuilder("select count(1) from detail_");
            sql4.append(code).append("_data").append(" where 1=1 ");
            if (StringUtils.isNoneBlank(startDate)) {
                sql4.append(" and insert_date >'").append(startDate).append("'");
            }
            if (StringUtils.isNoneBlank(endDate)) {
                sql4.append(" and insert_date <'").append(endDate).append("'");
            }
            Query query4 = entityManger.createNativeQuery(sql4.toString());
            int dataNum = Integer.parseInt(query4.getSingleResult().toString());
            projectNumCount.setDataNum(dataNum);
        } catch (Exception e) {
            LOG.error("该项目还没创建建数据表");
        }
        return projectNumCount;
    }

    public Map queryEchartDataList(String time,Map projectMap) {
        List<Object[]> list = projectinfoDAO.queryProjectCodeName();
        //查询近30天每天的数据量
        if (time.equalsIgnoreCase("day")) {
            for (int i = 0; i < list.size(); i++) {
                List<ChartDataEntity> list1 = new ArrayList<>();
                String code = list.get(i)[0].toString();
                StringBuilder selectDetail = new StringBuilder("SHOW TABLES LIKE 'detail_");
                selectDetail.append(code).append("_data'");
                String selectDetailsql = selectDetail.toString();
                Query query = entityManger.createNativeQuery(selectDetailsql);
                try {
                    query.getSingleResult();
                    StringBuilder sql6 = new StringBuilder("select v.day,ifnull(b.count,0) count from (\n" +
                            "SELECT CURDATE() AS `day` \n" +
                            "UNION all SELECT (CURDATE() - INTERVAL 1 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 2 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 3 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 4 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 5 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 6 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 7 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 8 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 9 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 10 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 11 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 12 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 13 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 14 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 15 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 16 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 17 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 18 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 19 Day) AS `day` \t\t\n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 20 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 21 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 22 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 23 Day) AS `day` \n" +
                            "UNION  all SELECT (CURDATE() - INTERVAL 24 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 25 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 26 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 27 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 28 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 29 Day) AS `day` \n" +
                            "UNION SELECT (CURDATE() - INTERVAL 30 Day) AS `day` \n" +
                            ") v left join\n" +
                            "(select DATE(insert_date)as 'day',count(*) as count\n" +
                            "    from detail_");
                    sql6.append(code).append("_data as d\n" +
                            "GROUP BY day\n" +
                            ")b on v.day = b.day \n" +
                            "order BY day");
                    Query query6 = entityManger.createNativeQuery(sql6.toString());
                    List<Object[]> countByMonth = query6.getResultList();
                    for (int a = 0; a < countByMonth.size(); a++) {
                        ChartDataEntity chartDataEntity = new ChartDataEntity();
                        Object[] o = countByMonth.get(a);
                        chartDataEntity.setData(o[1].toString());
                        chartDataEntity.setTime(o[0].toString());
                        list1.add(chartDataEntity);
                    }
                    ProjectNumCount  p = (ProjectNumCount)projectMap.get(code);
                    p.setProjectCharts(list1);
                } catch (Exception e) {
                    LOG.error("该项目还没创建建数据表");
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                List<ChartDataEntity> list2 = new ArrayList<>();
                String code = list.get(i)[0].toString();
                StringBuilder selectDetail = new StringBuilder("SHOW TABLES LIKE 'detail_");
                selectDetail.append(code).append("_data'");
                String selectDetailsql = selectDetail.toString();
                Query query = entityManger.createNativeQuery(selectDetailsql);
                try {
                    query.getSingleResult();
                    //查询近12个月每月的数据量
                    StringBuilder sql5 = new StringBuilder("select v.month,ifnull(b.count,0)  count from (\n" +
                            "    SELECT DATE_FORMAT(CURDATE(), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 1 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 2 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 3 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 4 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 5 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 6 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 7 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 8 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 9 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 10 MONTH), '%Y-%m') AS `month` \n" +
                            "    UNION SELECT DATE_FORMAT((CURDATE() - INTERVAL 11 MONTH), '%Y-%m') AS `month`\n" +
                            ") v \n" +
                            "left join\n" +
                            "(select \n" +
                            " left(insert_date,7) as 'month',count(*) as count\n" +
                            "from detail_");
                    sql5.append(code).append("_data as d\n" +
                            "where DATE_FORMAT(d.insert_date,'%Y-%m')>\n" +
                            "DATE_FORMAT(date_sub(curdate(), interval 12 month),'%Y-%m')\n" +
                            "GROUP BY month\n" +
                            ")b on v.month = b.month group by v.month");
                    Query query5 = entityManger.createNativeQuery(sql5.toString());
                    List<Object[]> countByYear = query5.getResultList();
                    for (int a = 0; a < countByYear.size(); a++) {
                        ChartDataEntity chartDataEntity = new ChartDataEntity();
                        Object[] o = countByYear.get(a);
                        chartDataEntity.setData(o[1].toString());
                        chartDataEntity.setTime(o[0].toString());
                        list2.add(chartDataEntity);
                    }
                    ProjectNumCount  p = (ProjectNumCount)projectMap.get(code);
                    p.setProjectCharts(list2);
                    } catch (Exception e) {
                    LOG.error("该项目还没创建建数据表");
                }
            }
            return  projectMap;
        }
        return  projectMap;
    }
}




