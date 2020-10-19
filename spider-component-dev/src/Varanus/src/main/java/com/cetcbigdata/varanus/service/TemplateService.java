package com.cetcbigdata.varanus.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.*;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.parser.WebClientDetailParser;
import com.cetcbigdata.varanus.parser.WebDriverDetailParser;
import com.cetcbigdata.varanus.utils.HumpToLine;
import com.cetcbigdata.varanus.utils.MapErgodic;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import com.cetcbigdata.varanus.utils.TemplateVerifyUtil;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;


/**
 * @author sunjunjie
 * @date 2020/8/17 17:28
 */
@Service
public class TemplateService {

    @Autowired
    private EntityManager entityManger;

    @Autowired
    private TemplateDAO templateDAO;

    @Autowired
    private TaskInfoDAO taskInfoDAO;

    @Autowired
    private ConfigListDAO configListDAO;

    @Autowired
    private SysDictDAO sysDictDAO;

    @Autowired
    private WebClientPool webClientPool;
    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    private WebClientDetailParser webClientDetailParser;

    @Autowired
    private WebDriverDetailParser webDriverDetailParser;

    @Autowired
    private TemplateVerifyUtil templateVerifyUtil;

    @Autowired
    private AuditLogService auditLogService;


    @PersistenceContext
    EntityManager em;

    ThreadLocal<WebClient> webClient = new ThreadLocal<>();

    ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);


    @Transactional
    public ErrorCode templateRun(int templateId, int isRun,int userId) {
        Timestamp runTime = new Timestamp(new Date().getTime());
        int i = templateDAO.templateRun(runTime, isRun, templateId);
        if (i > 0) {
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                        "修改"+templateId+"模板的启动状态为"+isRun,"");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
            }
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.TEMPLATE_START_FAIL;
    }

    @Transactional
    public ErrorCode templateDelete(int templateId,int userId)  {
        if (StringUtils.isNotBlank(configListDAO.findProjectIdByTemplateId(templateId))) {
            String code = configListDAO.findProjectIdByTemplateId(templateId).trim();
            int groupId = templateDAO.queryGroupIdBytempId(templateId);
            int i = templateDAO.countByGroupId(groupId);
             //如果i>1则表明该模板有共用模板，则只需要删除template表的数据，而无需删除list和detail表数据
            if (i>1) {
                try {
                    templateBasicDelete(templateId);
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "删除",
                            "删除id为" + templateId + "的模板", "");
                } catch (Exception e) {
                    LOG.error("删除" + SysModuleEnum.TEMPLATE.getName() + "日志失败！");
                }
            }
            else{
                try {
                    templateDetailDelete(code, groupId);
                } catch (Exception e) {
                    LOG.error("删除详情失败");
                    e.getMessage();
                }
                try {
                    templateListDelete(groupId);
                } catch (Exception e) {
                    LOG.error("删除列表失败");
                }
                try {
                    templateBasicDelete(templateId);
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "删除",
                            "删除id为" + templateId + "的模板", "");
                } catch (Exception e) {
                    LOG.error("删除" + SysModuleEnum.TEMPLATE.getName() + "日志失败！");
                }
                return ErrorCode.SUCCESS;
            }
        }
        return ErrorCode.TEMPLATE_DELETE_FAIL;
    }

    //根据template_id删除template表对应数据
    @Transactional
    public void templateBasicDelete(int templateId)  {
        templateDAO.templateDelete(templateId);
    }

    //根据listId删除config_list表对应数据
    @Transactional
    public void templateListDelete(int groupId)  throws Exception{
            templateDAO.templateListDelete(groupId);
    }

    //根据listId删除detail_xxx_config表对应数据
    @Transactional
    public void templateDetailDelete(String  code,int groupId)throws Exception {
            StringBuilder tableConfigName = new StringBuilder("detail_" + code + "_config");
            StringBuilder configSql = new StringBuilder("DELETE FROM " + tableConfigName);
            configSql.append(" where list_id= (select id from config_list where template_id=").append(groupId).append(")");
            String s = configSql.toString();
            Query query2 = entityManger.createNativeQuery(s);
            query2.executeUpdate();
    }

    @Transactional
    public ErrorCode templateClean(int templateId,int userId) {

        String code = configListDAO.findProjectIdByTemplateId(templateId).trim();
        StringBuilder tableConfigName = new StringBuilder("detail_" + code + "_data");
        try {
            //根据template_id删除detail_xxx_data表下模板istId对应的数据
            int listId = templateDAO.queryListIdByTempId(templateId);
            StringBuilder configSql = new StringBuilder("DELETE FROM " + tableConfigName + " where list_id=" + listId);
            String s = configSql.toString();
            Query query = entityManger.createNativeQuery(s);
            query.executeUpdate();
            auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "删除",
                    "清空id为"+templateId+"的模板的采集数据","");
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            LOG.error("数据清空失败");
            return ErrorCode.DATA_DELETE_FAIL;
        }
    }

    @Transactional
    public ErrorCode templateBreak(int templateId,int userId) {
        TemplateEntity templateEntity = templateDAO.findOne(templateId);
        int groupId = templateEntity.getGroupId();
        int i = templateDAO.countByGroupId(groupId);
        //i>1则该模板有共用，否则该模板无共用
        if (i > 1) {
            //groupId=templateId则说明该模板为共用模板的父模板，否则为子模版
            if (groupId == templateId) {
               // 为父模板时候不允许拆分
                return ErrorCode.TEMPLATE_BREAK_FAIL;
            } else {
                //为子模版拆分时，其原groupId清空，如果与其他模板共用则其groupId为其他共用模板的listId,否则为自身创建的listId
                templateDAO.deleteGroupId(templateId);
            }
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                        "拆分id为"+templateId+"的模板","");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
            }

        } else {
            return ErrorCode.TEMPLATE_BREAK_FAIL;
        }
        return ErrorCode.SUCCESS;
    }

    public int groupNumber(int templateId) {
        int groupId = templateDAO.queryGroupIdBytempId(templateId);
        int i = templateDAO.countByGroupId(groupId);
        return i;
    }

    @Transactional
    public int templateBasicSave(int userId,int isRun, int pageNumber, int docNumber, String templateUrl, int id, int taskId, String srcTypeCode, String domainCode) {
        try {
            //保存任务字段
            taskInfoDAO.saveTaskInfo(domainCode,srcTypeCode,taskId);
            if (id > 0) {
                //保存修改的模板
                templateDAO.templateBasicSave(id, taskId, templateUrl, docNumber, pageNumber, isRun);
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                            "修改id为"+id+"的模板基本信息","");
                } catch(Exception e){
                    LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
                }
                return id;
            } else {
                //新增模板
                 TemplateEntity templateEntity = new TemplateEntity();
                 templateEntity.setTaskId(taskId);
                 templateEntity.setListTemplateUrl(templateUrl);
                 templateEntity.setDocNumber(docNumber);
                 templateEntity.setListPageNumber(pageNumber);
                 templateEntity.setIsRun(isRun);
                 int id2 = templateDAO.saveAndFlush(templateEntity).getId();
                 //templateDAO.addTemplateBasic(taskId, templateUrl, docNumber, pageNumber, isRun);
                //将该模板所属的任务状态设置为1配置中
                taskInfoDAO.setTaskStateById(taskId);
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "新增",
                            "新增id为"+id2+"的模板基本信息","");
                } catch(Exception e){
                    LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
                }
                return id2;
            }
        } catch (Exception e) {
            LOG.error("模板保存失败");
        }
        return 0;
    }



    @Transactional
    public int templateListSave(ConfigListEntity configListEntity,int userId){
        int listId  =configListDAO.saveAndFlush(configListEntity).getId();
        int templateId =configListEntity.getTemplateId();
        templateDAO.setGroupId(listId,templateId);
        //将该模板所属的任务状态设置为1配置中
        taskInfoDAO.setTaskState(1,listId);
        try {
            auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                    "修改listId为"+listId+"的列表信息","");
        } catch(Exception e){
            LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
        }
        return listId;
    }

    @Transactional
    public ErrorCode templateDetailSave(HashMap<String, String> detailMap,HashMap<String, String> dynamicMap,int userId) {
        String detailId = detailMap.get("id");
        String listId = detailMap.get("listId");

        int di=0;
        if(StringUtils.isNoneBlank(detailId)) {
             di = Integer.parseInt(detailId);
        }
        int li = Integer.parseInt(listId);
        String projectCode = configListDAO.findProjectIdByListId(li).trim() ;
        StringBuilder tableConfigName = new StringBuilder("detail_" + projectCode + "_config");
        try {
            if (di > 0) {
                //当id存在，则为更新数据
                StringBuilder updateSql = new StringBuilder("update " + tableConfigName + " set ");
                StringBuilder detailsql = new StringBuilder(" ");
                StringBuilder dynamicsql = new StringBuilder(" ");
                StringBuilder endSql = new StringBuilder(" WHERE id=").append(di);
                detailMap.remove("id");
                MapErgodic.MapDetail(detailMap,detailsql);
                MapErgodic.MapDynamic(dynamicMap,dynamicsql);
                String s1 = updateSql.append(detailsql).append(dynamicsql).append(endSql).toString();
                Query query1 = entityManger.createNativeQuery(s1);
                query1.executeUpdate();
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                            "修改detailId为"+di+"的详情信息","");
                } catch(Exception e){
                    LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
                }
            } else {
                //当id不存在，则为插入数据
                StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableConfigName + " (");
                StringBuilder insertData = new StringBuilder("");
                detailMap.remove("id");
                for (Map.Entry<String, String> entry : detailMap.entrySet()) {
                    String key = entry.getKey();
                    String xpathValue = entry.getValue();
                    String s = HumpToLine.humpToLine(key);
                    insertSql.append(s + ",");
                    insertData.append("\""+xpathValue+"\"" + ",");
                }

                for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
                    String key = entry.getKey();
                    String s = HumpToLine.humpToLine(key);
                    StringBuilder sb = new StringBuilder(s);
                    String xpathName = sb.append("_xpath").toString();
                    String xpathValue = entry.getValue();
                    insertSql.append(xpathName + ",");
                    insertData.append("\""+xpathValue+"\""+ ",");
                }
                String is = insertSql.substring(0, insertSql.length() - 1);
                String id = insertData.substring(0, insertData.length() - 1);
                StringBuilder sql = new StringBuilder(is);
                String s2 = sql.append(") VALUES (").append(id).append(")").toString();
                Query query2 = entityManger.createNativeQuery(s2);
                query2.executeUpdate();
                try {
                    auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "新增",
                            "新增listId为"+li+"的列表详情信息","");
                } catch(Exception e){
                    LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
                }
            }
            //将该模板所属的任务状态设置为2已完成
            taskInfoDAO.setTaskState(2,li);
            Timestamp creatTime = new Timestamp(new Date().getTime());
            //该模板所属的任务状态设置为2时的时间
            taskInfoDAO.setStateUpdateTime(creatTime,li);
        } catch (Exception e) {
            LOG.error("详情数据更新失败");
            return ErrorCode.TEMPLATE_DETAIL_FAIL;
        }
        return ErrorCode.SUCCESS;
    }

    @Transactional
    public int templateMorelSave(int taskId,int userId){
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setTaskId(taskId);
        int templateId = templateDAO.saveAndFlush(templateEntity).getId();
        try {
            auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "新增",
                    "给taskId为"+taskId+"的任务新增的模板id为"+templateId,"");
        } catch(Exception e){
            LOG.error("新增"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
        }
        return templateId;
    }

    //复制模板信息
    public int copyConfigList(int othertemplateId){

        int groupId =configListDAO.findGroupIdByTemplateId(othertemplateId);
        return groupId;
    }

    //共用模板列表信息
    @Transactional
    public void templateSetGroup(int selfTemplateId,int otherTemplateId,int userId) {
        int groupId = templateDAO.queryGroupIdBytempId(otherTemplateId);
        StringBuilder selectConfig = new StringBuilder("UPDATE template SET group_id=" + groupId);
        String s = selectConfig.append(" WHERE id=").append(selfTemplateId).toString();
        Query query = entityManger.createNativeQuery(s);
        query.executeUpdate();
        try {
            auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                    "templateId为"+selfTemplateId+"的模板与templateId为"+otherTemplateId+"的模板信息共用","");
        } catch(Exception e){
            LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
        }
    }


    //获取模板信息概要列表
    public Page<Object> templateQuery(Pageable pageable, TemplateQueryEntity templateQueryEntity){

        StringBuilder sql = new StringBuilder(
                "select tm.id,p.name,ta.web_name,ta.section_title,tm.group_id,ta.section_url,tm.is_run,tm.is_correct \n" +
                        "from task_info ta INNER JOIN template tm ON ta.id=tm.task_id \n" +
                        "INNER JOIN project_info p ON ta.project_id=p.code where 1=1");

        String projectName =templateQueryEntity.getProjectName();
        String webName = templateQueryEntity.getWebName();
        String sectionTitle = templateQueryEntity.getSectionTitle();
        int userId = templateQueryEntity.getGetUserId();
        String isRun =  templateQueryEntity.getIsRun();
        int templateId = templateQueryEntity.getTemplateId();
        if (StringUtils.isNoneBlank(projectName)) {
            sql.append(" and p.name= '").append(projectName).append("'");
        }
        if (templateId>0) {
            sql.append("  and tm.id  = '").append(templateId).append("'");
        }
        if (StringUtils.isNoneBlank(webName)) {
            sql.append("  and ta.web_name = '").append(webName).append("'");
        }
        if (StringUtils.isNoneBlank(sectionTitle)) {
            sql.append("  and ta.section_title= '").append(sectionTitle).append("'");
        }
        if (StringUtils.isNoneBlank(isRun)) {
            sql.append("  and tm.is_run  =").append(isRun);
        }
        if(userId>0) {
            sql.append("  and ta.get_user_id= '").append(userId).append("'");
        }
        sql.append(" ORDER BY ta.update_time");
        return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, TaskTempInfo.class);
    }

    //获取模板基本信息
    public TemplateOneEntity templateOneBasic(int templateId){
        int taskId = templateDAO.queryTaskIdBytempId(templateId);
        TemplateEntity templateEntity = templateDAO.findOne(templateId);
        TaskInfoEntity taskInfoEntity = taskInfoDAO.findOne(taskId);
        TemplateOneEntity  templateOneEntity = new TemplateOneEntity();
        templateOneEntity.setTemplateEntity(templateEntity);
        templateOneEntity.setTaskInfoEntity(taskInfoEntity);
        return templateOneEntity;
    }

    //获取模板列表信息
    public ConfigListEntity templateOneList(int groupId){

        ConfigListEntity configListEntity = configListDAO.getOne(groupId);
        return configListEntity;
    }

    //获取模板详情信息
    public HashMap templateOneDetail(int listId){
        String projectCode = configListDAO.findProjectIdByListId(listId).trim() ;
        StringBuilder tableConfigName = new StringBuilder("detail_" + projectCode + "_config");
        StringBuilder sql = new StringBuilder("select * from ");
        String s = sql.append(tableConfigName).append(" where list_id=").append(listId).toString();
        Query query = entityManger.createNativeQuery(s);
        Object obj = query.getSingleResult();//detail_XX_config行值
        StringBuilder selectName = new StringBuilder("select column_name from information_schema.columns where table_name='"+"detail_" + projectCode+ "_config'");
        String s1= selectName.toString();
        Query query1 = entityManger.createNativeQuery(s1);
        List l2 = query1.getResultList();//detail_XX_config列名
        HashMap map = new HashMap();
        List<String> l1= JSON.parseArray(JSON.toJSONString(obj), String.class);
        for(int i = 0;i < l2.size(); i ++){
            map.put(l2.get(i),l1.get(i));
        }
        return map;
    }


    //获取模板信息概要列表
    public Page<Object> templateImportQuery(Pageable pageable,String json){
        JSONObject jsonObject = JSON.parseObject(json);
        String webName =jsonObject.getString("webName");
        String sectionTitle =jsonObject.getString("sectionTitle");
        String listId = jsonObject.getString("listId");
        StringBuilder sql = new StringBuilder(
                "select cl.id,ta.web_name,ta.section_title,ta.section_url,cl.list_xpath" +
                        " from task_info ta INNER JOIN template tm ON ta.id=tm.task_id " +
                        " INNER JOIN config_list cl on cl.template_id=tm.id where 1=1 ");
        if (StringUtils.isNoneBlank(listId)) {
            sql.append("  and tm.id  = '").append(listId).append("'");
        }
        if (StringUtils.isNoneBlank(webName)) {
            sql.append("  and ta.web_name = '").append(webName).append("'");
        }
        if (StringUtils.isNoneBlank(sectionTitle)) {
            sql.append("  and ta.section_title= '").append(sectionTitle).append("'");
        }
        sql.append(" order by ta.web_name ,ta.section_title,ta.update_time");
        return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, TemplateImportEntity.class);
    }


    //模板列表信息验证
    public Object templateListVerify(ConfigListEntity configListEntity) throws Exception {
                int templateId = configListEntity.getTemplateId();
                //网站地址
                String sectionUrl = taskInfoDAO.selectUrlByTemplateId(templateId);
                TemplateVerifyUtil templateVerifyUtil =new TemplateVerifyUtil();
                return JSON.toJSONString(templateVerifyUtil.templateListVerify(configListEntity,sectionUrl,webClientPool, webDriverFactory,templateDAO));
    }


    //查询领域和来源
    public Object domainAnddataSrcType()  {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("domain",sysDictDAO.queryDomain());
        jsonObject.put("dataSrcType",sysDictDAO.queryDataSrcType());
        return jsonObject;
    }

    @Transactional
    //修改模板的验证状态
    public ErrorCode templateIsCorrect(int templateId,int isCorrect,int userId)  {

        int i = templateDAO.templateIsCorrect(templateId,isCorrect);
        if(i>0){
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.TEMPLATE, "修改",
                        "修改templateId为"+templateId+"的模板状态为"+isCorrect,"");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
            }
            return  ErrorCode.SUCCESS;
        }
        return ErrorCode.SAVE_FAILD;
    }


    //模板详情信息验证
     public Map templateDetailVerify(String json) throws Exception{

         try {
             JSONObject jsonObject = JSON.parseObject(json);
             String templateDetail = jsonObject.getString("templateDetailEntity");
             String templateDynamic =jsonObject.getString("templateDetailDynamic");
             HashMap<String, String> detailMap=JSON.parseObject(templateDetail, HashMap.class);
             HashMap<String,String> dynamicMap=JSON.parseObject(templateDynamic, HashMap.class);
             String id =  detailMap.get("listId");
             String webName = configListDAO.findWebNameByListId(Integer.parseInt(id));
             String url = detailMap.get("testUrl");
             String title = detailMap.get("title");
             //detailMap存固定值 dynamicMap存动态Xpath
             dynamicMap.put("title",title);
             Map detailData  = new HashMap();
             // 判断需要验证的配置
             if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(detailMap.get("clientType"))) {
                 webClient.set(webClientPool.getFromPool(Integer.parseInt(detailMap.get("needProxy")) == 1 ? true : false));
                 WebRequest webRequest = PageParserHelper.getWebRequest(url, HttpMethod.GET);
                 HtmlPage htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                 if (Optional.ofNullable(htmlPage).isPresent()) {
                     webClientDetailParser.htmlPageParser(dynamicMap,htmlPage,detailMap,detailData,webName);
                 }
             }
             if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(detailMap.get("clientType"))) {
                 webDriverThreadLocal.set(webDriverFactory.get());
                 webDriverThreadLocal.get().get(url);
                 Thread.sleep(3000);
                 webDriverDetailParser.webDriverParser(dynamicMap,detailMap,detailData,webName, webDriverThreadLocal.get());
             }
             return detailData;
         } catch (Exception e) {
             LOG.error("验证详情出错", e);
             throw e;
         } finally {
             if (webClient.get() != null) {
                 webClient.get().close();
             }
             WebDriver webDriver = webDriverThreadLocal.get();
             if (webDriver != null) {
                 webDriverFactory.close(webDriverThreadLocal.get());
             }
             webDriverThreadLocal.remove();
         }
     }

   //先拿到导入模板的Xpath值再放入url进行校验并向前端返回校验值
    public Object templateImportVerify(int templateId,String listUrl,String detailUrl) throws Exception {

        int groupId = configListDAO.findGroupIdByTemplateId(templateId);
        ConfigListEntity configListEntity =  configListDAO.findOne(groupId);
        JSONObject jsonObject = new JSONObject(true);
        //验证列表信息
        if (configListEntity != null) {
            Object o1 = templateVerifyUtil.templateListVerify(configListEntity, listUrl,webClientPool, webDriverFactory,templateDAO);
            jsonObject.put("list",o1);
        }
        //验证详情信息
        //查询出detail的信息
        Map<String, String> detailConfig = templateOneDetail(groupId);
        if (detailConfig != null) {
            Map detailData  = new HashMap();
            String webName = templateDAO.queryWebNameByTempId(templateId);
            Object o2 =templateVerifyUtil.templateDetailVerify(detailConfig,detailUrl,detailData,webName,webClientPool,
                    webDriverFactory,webClientDetailParser,webDriverDetailParser);
            jsonObject.put("detail",o2);
        }
     return jsonObject;
    }

    //先拿到导入模板的Xpath值与url进行校验并向前端返回校验值
    public Object templateTotalVerify(int templateId) throws Exception {

        int groupId = configListDAO.findGroupIdByTemplateId(templateId);
        String listUrl =taskInfoDAO.selectUrlByTemplateId(templateId);
        String code = configListDAO.findProjectIdByTemplateId(templateId);
        StringBuilder tableConfigName = new StringBuilder("detail_"+code+"_config");
        StringBuilder configSql = new StringBuilder("select test_url from " + tableConfigName + " where id = ");
        configSql.append(groupId);
        String s = configSql.toString();
        Query query = entityManger.createNativeQuery(s);
        String  url = "";
        if(query.getResultList()!=null) {
            url= query.getResultList().toString();
        }
        String detailUrl = url.replace("[","").replace("]","");
        ConfigListEntity configListEntity =  configListDAO.findOne(groupId);
        TemplateVerifyUtil templateVerifyUtil = new TemplateVerifyUtil();

        //验证列表信息
        JSONObject jsonObject = new JSONObject(true);
        if (configListEntity != null) {
            Object o1 = templateVerifyUtil.templateListVerify(configListEntity, listUrl,webClientPool, webDriverFactory,templateDAO);
            if(o1!=null){
                jsonObject.put("list",o1);
            }
        }
        //验证详情信息
        //查询出detail的信息
        HashMap<String, String> detailConfig = templateOneDetail(groupId);
        if (detailConfig != null) {
            HashMap detailData  = new HashMap();
            String webName = templateDAO.queryWebNameByTempId(templateId);
            Object o2 =templateVerifyUtil.templateDetailVerify(detailConfig,detailUrl,detailData,webName,
                    webClientPool, webDriverFactory,webClientDetailParser,webDriverDetailParser);
            jsonObject.put("detail",o2);
        }
        return jsonObject;
    }
}
