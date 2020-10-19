package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.DocDetailDAO;
import com.cetcbigdata.varanus.dao.ListDetailDAO;
import com.cetcbigdata.varanus.dao.OfficialDocumentDAO;
import com.cetcbigdata.varanus.dao.TaskBasicInfoDAO;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.DailyWarning;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.ListCrawlerdataCount;
import com.cetcbigdata.varanus.entity.ListDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.entity.OfficialQuery;
import com.cetcbigdata.varanus.entity.TaskBasicInfo;
import com.cetcbigdata.varanus.entity.TaskWarning;
import com.cetcbigdata.varanus.parser.WebClientDocParser;
import com.cetcbigdata.varanus.parser.WebDriverDocParser;
import com.cetcbigdata.varanus.service.ListDetailService;
import com.cetcbigdata.varanus.service.OfficialDocumentService;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created with IDEA author:Matthew Date:2019-4-23 Time:9:29
 */
@RestController
public class CrawlerController {

    @Autowired
    private WebClientPool webClientPool;
    @Autowired
    private WebClientDocParser webClientDocParser;
    @Autowired
    private WebDriverDocParser webDriverDocParser;
    @Autowired
    private TaskBasicInfoDAO taskBasicInfoDAO;
    @Autowired
    private ListDetailDAO listDetailDAO;
    @Autowired
    private TaskWarningDAO taskWarningDao;
    @Autowired
    private OfficialDocumentService officialDocumentService;
    @Autowired
    private WebDriverFactory webDriverFactory;
    @Autowired
    private ListDetailService listDetailService;
    @Autowired
    private DocDetailDAO docDetailDAO;
    @Autowired
    private OfficialDocumentDAO officialDocumentDao;




    ThreadLocal<WebClient> webClient = new ThreadLocal<>();
    ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    private static final Logger LOG = LoggerFactory.getLogger(CrawlerController.class);

    /**
     * 列表验证
     *
     * @param json
     * @return
     * @throws Exception
     */
/*    @GetMapping("list/verify")
    public Object index(@RequestParam("json") String json) throws Exception {

        if (StringUtils.isBlank(json)) {
            return "参数错误";
        }
        ListDetail listDetail;
        TaskBasicInfo taskBasicInfo;
        try {
            listDetail = JSON.parseObject(json, ListDetail.class);
            taskBasicInfo = JSON.parseObject(json, TaskBasicInfo.class);
        } catch (Exception e) {
            return "参数错误";
        }
        webClient.set(webClientPool.getFromPool(listDetail.getListNeedProxy() == 1 ? true : false));
        WebRequest webRequest = null;
        HtmlPage htmlPage;
        List<DomNode> domElementList = new ArrayList<>();
        List<OfficialDocument> officialDocuments = new LinkedList<>();
        try {
            if (listDetail != null) {
                if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(listDetail.getListClientType())) {
                    if (listDetail.getListRequestType().equals(HttpMethod.GET.name())) {
                        LOG.info("使用Webclient GET方法验证");
                        webRequest = PageParserHelper.getWebRequest(taskBasicInfo.getSectionUrl(), HttpMethod.GET);
                    } else if (listDetail.getListRequestType().equals(HttpMethod.POST.name())) {
                        LOG.info("使用Webclient POST方法验证");
                        webRequest = PageParserHelper.getWebRequest(taskBasicInfo.getSectionUrl(), HttpMethod.POST);
                    }
                    if (!StringUtils.isBlank(listDetail.getListRequestParams())) {
                        //增加访问参数
                        PageParserHelper.jsonPairsToMap(listDetail.getListRequestParams(), webRequest);
                    }
                    //增加浏览器头部信息
                    if (!StringUtils.isBlank(listDetail.getListPageHeader())) {
                        PageParserHelper.jsonHeaders(listDetail.getListPageHeader(), webRequest);
                    }
                    if (Constants.DOC_RESPONSETYPE_HTML.equals(listDetail.getListResponseType())) {
                        LOG.info("使用Webclient 网页返回值为HTML");
                        htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                        domElementList = PageParserHelper.getDomNodeList(htmlPage, listDetail.getListXpath());
                          PageParserHelper.parseHTMLList(domElementList, listDetail, officialDocuments, htmlPage);
                    }
                    if (Constants.DOC_RESPONSETYPE_XML.equals(listDetail.getListResponseType())) {
                        LOG.info("使用Webclient 网页返回值为XML");
                        htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                        domElementList = PageParserHelper.getDomNodeList(htmlPage, listDetail.getListXpath());
                        PageParserHelper.parseHTMLList(domElementList, listDetail, officialDocuments, htmlPage);
                    }
                    if (Constants.DOC_RESPONSETYPE_JSON.equals(listDetail.getListResponseType())) {
                        String jsonField = listDetail.getListJsonField();
                        String jsonId = listDetail.getJsonIdKey();
                        // 根据jsonfield的内容获取json返回中的哪些字段应该取回
                        if (!StringUtils.isBlank(jsonField) || !StringUtils.isBlank(jsonId)) {
                            // 通过json处理请求
                            JSONObject jsonObject = JSON.parseObject(jsonField);
                            LOG.info("使用Webclient 网页返回值为json");
                            PageParserHelper.parseJsonList(webClient, jsonObject, listDetail, officialDocuments);
                        }
                    }
                }
                if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(listDetail.getListClientType())) {
                    LOG.info("使用Webdriver 验证");
                    webDriverThreadLocal.set(webDriverFactory.get());
                    webDriverThreadLocal.get().get(taskBasicInfo.getSectionUrl());
                    PageParserHelper.parseWebDriverList(officialDocuments, listDetail, webDriverThreadLocal.get());
                }
            }*/

/*            //对于列表中不规范的url进行处理(如上海警察局)
            if(Optional.ofNullable(listDetail.getListHref()).isPresent()) {
                Integer flage = listDetail.getListHref();
                if (flage == 1) {
                    officialDocuments.clear();
                    WebDriver webDriver = webDriverFactory.get();
                    webDriverThreadLocal.set(webDriver);
                    webDriverThreadLocal.get().get(taskBasicInfo.getSectionUrl());
                    List<WebElement> domNodeList = webDriver.findElements(By.xpath(listDetail.getListXpath()));
                    for (WebElement webElement : domNodeList) {
                        OfficialDocument officialDocument = new OfficialDocument();
                        String title = webElement.getText();
                        try {
                            if (webElement.isEnabled() && webElement.isDisplayed()) {
                                LOG.info("使用JS进行页面元素单击");
                                //执行JS语句arguments[0].click();
                                ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", webElement);
                            } else {
                                LOG.error("页面上元素无法进行单击操作");
                            }
                        } catch (StaleElementReferenceException e) {
                            LOG.error("页面元素没有附加在页面中" + Arrays.toString(e.getStackTrace()));
                        } catch (NoSuchElementException e) {
                            LOG.error("在页面中没有找到要操作的元素" + Arrays.toString(e.getStackTrace()));
                        } catch (Exception e) {
                            LOG.error("无法完成单击操作" + Arrays.toString(e.getStackTrace()));
                        }
                        String currentWindow = webDriver.getWindowHandle();//获取当前窗口句柄
                        Set<String> handles = webDriver.getWindowHandles();//获取所有窗口句柄
                        Iterator<String> it = handles.iterator();
                        String currentUrl = null;
                        while (it.hasNext()) {
                            if (currentWindow == it.next()) {
                                continue;
                            }
                            WebDriver window = webDriver.switchTo().window(it.next());//切换到新窗口
                            currentUrl = window.getCurrentUrl();
                            window.close();//关闭新窗口
                        }
                        webDriver.switchTo().window(currentWindow);//回到原来页面
                        officialDocument.setUrl(currentUrl);
                        officialDocument.setTitle(title);
                        officialDocuments.add(officialDocument);
                    }
                    webDriver.quit();
                }
            }
            return JSON.toJSONString(officialDocuments);

        } catch (Exception e) {
            LOG.error("验证错误", e);
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
            webClient.remove();
        }

    }*/


    /**
     * 列表保存
     *
     * @param json
     * @return
     */
    @GetMapping("list/save")
    @Transactional
    public String saveSite(@RequestParam("json") String json) {
        LOG.info(json);
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            TaskBasicInfo taskBasicInfo = JSON.toJavaObject(jsonObject, TaskBasicInfo.class);
            taskBasicInfo.setSqlTable("official_document");
            ListDetail listDetail = JSON.toJavaObject(jsonObject, ListDetail.class);
            // 先写入task表，再写入listDetail表
            taskBasicInfo = taskBasicInfoDAO.save(taskBasicInfo);
            listDetail.setTaskId(taskBasicInfo.getTaskId());
            listDetail = listDetailDAO.save(listDetail);
            Map map = new HashMap<>();
            map.put("taskId", taskBasicInfo.getTaskId());
            map.put("listId", listDetail.getListId());
            return JSON.toJSONString(map);
        } catch (Exception e) {
            LOG.error("保存网站配置出错", e);
        }
        return "";
    }

    //list编辑接口
    @GetMapping("list/one")
    public Object findlistOne(@RequestParam("taskId") Integer taskId) {
        try {
            return listDetailDAO.queryOneList(taskId);
        } catch (Exception e) {
            LOG.error("查询列表信息出错", e);
        }
        return "error";
    }


    @GetMapping("list/view")
    public Object siteList(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                           @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                           @RequestParam(value = "json", required = false) String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            OfficialQuery officialQuery = JSON.toJavaObject(jsonObject, OfficialQuery.class);
            Page listDetails = listDetailService.queryListDetailByCondition(pageable, officialQuery);
            return listDetails;
        } catch (Exception e) {
            LOG.error("查询基本信息出错", e);
        }
        return "";
    }



    @GetMapping("list/edit")
    @Transactional
    public Object siteEdit(@RequestParam("json") String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        TaskBasicInfo taskBasicInfo = JSON.toJavaObject(jsonObject, TaskBasicInfo.class);
        taskBasicInfo.setSqlTable("official_document");
        ListDetail listDetail = JSON.toJavaObject(jsonObject, ListDetail.class);
        if (taskBasicInfo.getTaskId() == null || listDetail.getListId() == null) {
            return "error";
        }
        // 先写入task表，再写入listDetail表
        taskBasicInfo = taskBasicInfoDAO.save(taskBasicInfo);
        listDetail.setTaskId(taskBasicInfo.getTaskId());
        listDetail = listDetailDAO.save(listDetail);
        Map map = new HashMap<>();
        map.put("taskId", taskBasicInfo.getTaskId());
        map.put("listId", listDetail.getListId());
        return JSON.toJSONString(map);
    }

    /**
     * 任务删除
     *
     * @param taskId
     * @return
     */
    @GetMapping("task/delete")
    @Transactional
    public String siteDelete(@RequestParam("taskId") Integer taskId) {
        TaskBasicInfo taskBasicInfo = taskBasicInfoDAO.findByTaskId(taskId);
        if (Optional.ofNullable(taskBasicInfo).isPresent()) {
            taskBasicInfoDAO.delete(taskId);
            listDetailDAO.deleteTask(taskId);
            docDetailDAO.deleteTask(taskId);
            return "success";
        }
        return "success";
    }

    /**
     * 详情页删除
     *
     * @param docId
     * @return
     */
    @GetMapping("detail/delete")
    @Transactional
    public String siteEdit(@RequestParam("docId") Integer docId) {
        docDetailDAO.delete(docId);
        return "success";
    }

    @GetMapping("task/start")
    @Transactional
    public String startTask(Integer isValid, Integer taskId) {
        Integer integer = taskBasicInfoDAO.updateTaskStatus(isValid, taskId);
        if (integer > 0) {
            return "successs";
        } else {
            return "fail";
        }
    }

    @GetMapping("detail/verify")
    public String detailVerify(@RequestParam("json") String json) throws Exception {
        LOG.info(json);
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            DocDetail docDetail = JSON.toJavaObject(jsonObject, DocDetail.class);
            // 判断需要验证的配置
            String url = docDetail.getTestUrl();
            OfficialDocument officialDocument = new OfficialDocument();
            if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(docDetail.getDocClientType())) {
                webClient.set(webClientPool.getFromPool(docDetail.getDocNeedProxy() == 1 ? true : false));
                WebRequest webRequest = PageParserHelper.getWebRequest(url, HttpMethod.GET);
                HtmlPage htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                if (Optional.ofNullable(htmlPage).isPresent()) {
                    //webClientDocParser.htmlPageParser(docDetail, officialDocument, htmlPage);
                }
            }
            if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(docDetail.getDocClientType())) {
                webDriverThreadLocal.set(webDriverFactory.get());
                webDriverThreadLocal.get().get(docDetail.getTestUrl());
                Thread.sleep(3000);
                webDriverDocParser.webDriverParser(docDetail, officialDocument, webDriverThreadLocal.get());
            }
            // 按照把有内容的字段排在前面
            String result = JSON.toJSONString(officialDocument);
            return result;
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

	/*
     * @GetMapping("detail/view") public Object siteDetail() { try { List<DocDetail>
	 * docDetails =
	 * docDetailDAO.findAll().stream().sorted(Comparator.comparing(DocDetail::
	 * getDocId).reversed()).collect(toList()); List<Map> result = new
	 * LinkedList<>(); docDetails.forEach(dd->{ Map<String, Object> map = new
	 * LinkedHashMap<>(); TaskBasicInfo taskBasicInfo =
	 * taskBasicInfoDAO.findByTaskId(dd.getTaskId()); try {
	 * Util.objectToMap(taskBasicInfo, map); Util.objectToMap(dd, map);
	 * result.add(map); } catch (Exception e) { LOG.error("查询详情出错", e); }
	 *
	 * }); return result; } catch (Exception e) { LOG.error("查询详情出错", e); } return
	 * ""; }
	 */

    @GetMapping("detail/save")
    @Transactional
    public String sdaveDetail(@RequestParam("json") String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        DocDetail docDetail = JSON.toJavaObject(jsonObject, DocDetail.class);
        docDetailDAO.save(docDetail);
        return "success";
    }

    @GetMapping("warning/list")
    public Object getDailyWarning(@RequestParam("warningDate") String warningDate) {
        List<DailyWarning> warningList = new ArrayList<DailyWarning>();
        try {
            // 将MM-dd-yyyy改成yyyy-MM-dd
            SimpleDateFormat dMy = new SimpleDateFormat("MM-dd-yyyy");
            Date date = dMy.parse(warningDate);
            SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
            warningDate = yMd.format(date);

            Object[] warningObjects = taskWarningDao.findDailyWarning(warningDate);
            for (Object object : warningObjects) {
                Object[] warning = (Object[]) object;
                DailyWarning dailyWarning = new DailyWarning((Integer) warning[0], (BigInteger) warning[1],
                        (BigInteger) warning[2], (BigInteger) warning[3], (BigInteger) warning[4],
                        (BigInteger) warning[5], (BigInteger) warning[6]);
                warningList.add(dailyWarning);
            }
        } catch (Exception e) {
            LOG.error("查询报警列表出错", e);
        }
        return warningList;
    }

    @GetMapping("warning/detail")
    public Object getDailyWarningByTaskId(@RequestParam("taskId") int taskId,
                                          @RequestParam("warningDate") String warningDate) {
        List<TaskWarning> warningList = null;
        try {
            // 将MM-dd-yyyy改成yyyy-MM-dd
            SimpleDateFormat dMy = new SimpleDateFormat("MM-dd-yyyy");
            Date date = dMy.parse(warningDate);
            SimpleDateFormat yMd = new SimpleDateFormat("yyyy-MM-dd");
            warningDate = yMd.format(date);

            warningList = taskWarningDao.findDailyWarningByTaskId(taskId, warningDate);
            for (TaskWarning warning : warningList) {
                if (warning.getListWarningType() != null) {
                    Integer listWarningType = warning.getListWarningType();
                    switch (listWarningType) {
                        case 0:
                            warning.setListWarningReason(Constants.NETWORK_WARNING_REASON);
                            break;
                        case 1:
                            warning.setListWarningReason(Constants.URL_WARNING_REASON);
                            break;
                        case 2:
                            warning.setListWarningReason(Constants.TASK_TEMPLATE_WARNING_REASON);
                            break;
                    }
                }
                if (warning.getDocWarningType() != null) {
                    Integer docWarningType = warning.getDocWarningType();
                    switch (docWarningType) {
                        case 0:
                            warning.setDocWarningReason(Constants.NETWORK_WARNING_REASON);
                            break;
                        case 1:
                            warning.setDocWarningReason(Constants.URL_WARNING_REASON);
                            break;
                        case 2:
                            warning.setDocWarningReason(Constants.TASK_TEMPLATE_WARNING_REASON);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("查询报警详情出错", e);
        }
        return warningList;
    }

    @GetMapping("test/detail")
    public Object testDetail() {
        return docDetailDAO.findDocDetails();
    }

    /**
     * 查询OfficialDocument
     */
    @GetMapping("detail/document")
    public Object siteOffice(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer size) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            Page od = officialDocumentService.queryListofficial(pageable);

            return od;
        } catch (Exception e) {
            LOG.error("查询基本信息出错", e);
        }
        return "";
    }

//爬虫数据查找的接口
    @GetMapping("list/documentQuery")
    public Object siteOffice(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer size, @RequestParam(value="json",defaultValue = "{}") String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            OfficialQuery officialQuery = JSON.toJavaObject(jsonObject, OfficialQuery.class);
            Page od = officialDocumentService.queryListCrawlerdata(pageable, officialQuery);

            return od;
        } catch (Exception e) {
            LOG.error("查询基本信息出错", e);
        }
        return "";
    }

    /**
     * 统计采集url数量
     */
    @GetMapping("detail/urlcount")
    public Object siteUrlcount(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer size, @RequestParam(value = "json", defaultValue = "{}") String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            OfficialQuery officialQuery = JSON.toJavaObject(jsonObject, OfficialQuery.class);
            Page od = officialDocumentService.queryListCrawlerdata4(pageable, officialQuery);
             int odSize = od.getContent().size();
             for (int i=0; i < odSize; i++) {
                 Integer pageUrlCount = ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).getPageUrlCount();
                 Integer listPageNumber = ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).getListPageNumber();
                 Integer sectionDocCount = ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).getSectionDocCount();
                 if(pageUrlCount == null || pageUrlCount == 0) {
                    /* pageUrlCount = index(JSON.toJSONString(((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)))).
                             toString().split("\"url\":").length -1;*/
                     if(pageUrlCount == null || pageUrlCount > 30) {
                         pageUrlCount = 20;
                     }



                 }
                 Integer webUrlCount = pageUrlCount*listPageNumber;
                 ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).setWebUrlCount(webUrlCount);
                 ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).setDifferenceUrlCount(webUrlCount-sectionDocCount);
                 if(webUrlCount == 0 || webUrlCount == null) {
                     ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).setSuccessRate("100%");
                 } else {
                     ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(i)).setSuccessRate(((webUrlCount-sectionDocCount)*100/webUrlCount)+"%");
                 }

             }


           // ((ListCrawlerdataCount) ((PageImpl) od).getContent().get(0)).
            return od;
        } catch (Exception e) {
            LOG.error("查询基本信息出错", e);
        }
        return "";
    }

    @GetMapping("crawler/datasrctype")
    public Object getDataSrcTypeName() {
        return taskBasicInfoDAO.findDataSrcTypeName();
    }
    @GetMapping("crawler/domain")
    public Object getDomain() {
        return taskBasicInfoDAO.findDomains();
    }

    @GetMapping("crawler/peoples")
    public Object getresponsiblePeoples() {
        return taskBasicInfoDAO.findAllByResponsiblePeoples();
    }

    @GetMapping("crawler/getdepartment")
    public Object getDepartment(@RequestParam("department")String department) {
        return taskBasicInfoDAO.findDepartment(department);
    }
    @GetMapping("crawler/getSectiontitle")
    public Object getSectiontitle(@RequestParam("department")String department) {
        return taskBasicInfoDAO.findSectiontitle(department);
    }

    @GetMapping("crawler/departments")
    public Object getdepartments() {
        return taskBasicInfoDAO.findAllBydepartments();
    }

    @GetMapping("crawler/taskIds")
    public Object gettaskIds() {
        return taskBasicInfoDAO.findAllByTaskIds();
    }

    @GetMapping("detail/view")
    public Object siteDetail(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                             @RequestParam(value = "json", required = false) String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            OfficialQuery officialQuery = JSON.toJavaObject(jsonObject, OfficialQuery.class);
            Page od = officialDocumentService.queryListOfficialByofficial(pageable, officialQuery);
            return od;
        } catch (Exception e) {
            LOG.error("查询详情出错", e);
        }
        return "";
    }

    @GetMapping("detail/one")
    public Object getDocDetail(String keyId) {
        return officialDocumentDao.findOfficialDocumentByKeyId(keyId);
    }


    @GetMapping("detail/update")
    @Transactional
    public Object getdoctail(@RequestParam(value = "taskId") Integer taskId) {
        DocDetail docDetail = new DocDetail();
        List<DocDetail> docDetails = docDetailDAO.findByTaskId(taskId);
        if (!CollectionUtils.isEmpty(docDetails)) {
            docDetail = docDetails.get(0);
        }
        else{
            docDetail.setTaskId(taskId);
            docDetail.setListId(taskId);
        }
        return docDetail;
    }

    @GetMapping("data/delete")
    public  String dataDelete(@RequestParam("taskId") Integer taskId,
                             @RequestParam(value = "listId") Integer listId) {
        officialDocumentService.dataDelete(taskId,listId);
        return "success";
    }

    @GetMapping("detail/docidToUrl")
    public List<String> docidToUrl(@RequestParam(value = "docId") Integer docId){
        List<String> url = officialDocumentDao.findUrlByDocId(docId);
        return url;
    }
}
