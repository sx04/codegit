package com.cetcbigdata.varanus.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TemplateDAO;
import com.cetcbigdata.varanus.entity.ConfigListEntity;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.parser.WebClientDetailParser;
import com.cetcbigdata.varanus.parser.WebDriverDetailParser;
import com.cetcbigdata.varanus.service.TemplateService;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.*;

/**
 * @author sunjunjie
 * @date 2020/8/26 17:10
 */
@Component
public class TemplateVerifyUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);

    ThreadLocal<WebClient> webClient = new ThreadLocal<>();

    ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();


    public Object templateListVerify(ConfigListEntity configListEntity, String sectionUrl,
                                     WebClientPool webClientPool,WebDriverFactory webDriverFactory,TemplateDAO templateDAO) throws Exception {

        webClient.set(webClientPool.getFromPool(configListEntity.getListNeedProxy() == 1 ? true : false));
        WebRequest webRequest = null;
        HtmlPage htmlPage;
        List<DomNode> domElementList = new ArrayList<>();
        //TaskInfoEntity用于存储检验list时返回的列表页文章的url和标题
        List<TaskInfoEntity> taskInfoEntities = new LinkedList<>();
        int templateId = configListEntity.getTemplateId();
        try {
            if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(configListEntity.getListClientType())) {
                if (configListEntity.getListRequestType().equals(HttpMethod.GET.name())) {
                    LOG.info("使用Webclient GET方法验证");
                    webRequest = PageParserHelper.getWebRequest(sectionUrl, HttpMethod.GET);
                } else if (configListEntity.getListRequestType().equals(HttpMethod.POST.name())) {
                    LOG.info("使用Webclient POST方法验证");
                    webRequest = PageParserHelper.getWebRequest(sectionUrl, HttpMethod.POST);
                }
                if (!StringUtils.isBlank(configListEntity.getListRequestParams())) {
                    //增加访问参数
                    PageParserHelper.jsonPairsToMap(configListEntity.getListRequestParams(), webRequest);
                }
                //增加浏览器头部信息
                if (!StringUtils.isBlank(configListEntity.getListPageHeader())) {
                    PageParserHelper.jsonHeaders(configListEntity.getListPageHeader(), webRequest);
                }
                if (Constants.DOC_RESPONSETYPE_HTML.equals(configListEntity.getListResponseType())) {
                    LOG.info("使用Webclient 网页返回值为HTML");
                    htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                    domElementList = PageParserHelper.getDomNodeList(htmlPage, configListEntity.getListXpath());
                    PageParserHelper.parseHTMLList(domElementList, configListEntity, taskInfoEntities, htmlPage);
                }
                if (Constants.DOC_RESPONSETYPE_XML.equals(configListEntity.getListResponseType())) {
                    LOG.info("使用Webclient 网页返回值为XML");
                    htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
                    domElementList = PageParserHelper.getDomNodeList(htmlPage, configListEntity.getListXpath());
                    PageParserHelper.parseHTMLList(domElementList, configListEntity, taskInfoEntities, htmlPage);
                }
                if (Constants.DOC_RESPONSETYPE_JSON.equals(configListEntity.getListResponseType())) {
                    String jsonField = configListEntity.getListJsonField();
                    String jsonId = configListEntity.getJsonIdKey();
                    // 根据jsonfield的内容获取json返回中的哪些字段应该取回
                    if (!StringUtils.isBlank(jsonField) || !StringUtils.isBlank(jsonId)) {
                        // 通过json处理请求
                        JSONObject jsonObject = JSON.parseObject(jsonField);
                        LOG.info("使用Webclient 网页返回值为json");
                        String listTemplateUrl = templateDAO.queryListTemplateUrlByTempId(templateId);
                        PageParserHelper.parseJsonList(webClient, jsonObject, configListEntity, taskInfoEntities, listTemplateUrl);
                    }
                }
            }
            if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(configListEntity.getListClientType())) {
                LOG.info("使用Webdriver 验证");
                webDriverThreadLocal.set(webDriverFactory.get());
                webDriverThreadLocal.get().get(sectionUrl);
                PageParserHelper.parseWebDriverList(taskInfoEntities, configListEntity, webDriverThreadLocal.get());
            }

            //对于列表中不规范的url进行处理(如上海警察局)
            if (configListEntity.getListHref() > 0) {
                WebDriver webDriver = webDriverFactory.get();
                PageParserHelper.parseListByClick(webDriverThreadLocal, configListEntity, webDriver, taskInfoEntities, sectionUrl);
            }
            return taskInfoEntities;
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
    }

    public Object templateDetailVerify(Map<String, String> detailConfig,String detailUrl, Map detailData, String webName,
                                       WebClientPool webClientPool, WebDriverFactory webDriverFactory, WebClientDetailParser webClientDetailParser
            , WebDriverDetailParser webDriverDetailParser) throws Exception {

        try{
        //detailMap存固定的值
        HashMap detailMap = new HashMap();
        //detailConfig用于存动态的值，移除其中的固定值
        detailConfig.remove("id");
        detailConfig.remove("list_id");
        detailMap.put("client_type", detailConfig.get("client_type"));
        detailConfig.remove("client_type");
        detailMap.put("request_type", detailConfig.get("request_type"));
        detailConfig.remove("request_type");
        detailMap.put("request_params", detailConfig.get("request_params"));
        detailConfig.remove("request_params");
        detailMap.put("response_type", detailConfig.get("response_type"));
        detailConfig.remove("response_type");
        detailMap.put("json_field", detailConfig.get("json_field"));
        detailConfig.remove("json_field");
        detailMap.put("need_proxy", detailConfig.get("need_proxy"));
        detailConfig.remove("need_proxy");
        detailConfig.remove("test_url");
        detailMap.put("analyze_type", detailConfig.get("analyze_type"));
        detailConfig.remove("analyze_type");
        detailMap.put("interface_id", detailConfig.get("interface_id"));
        detailConfig.remove("interface_id");
        detailMap.put("site_width", detailConfig.get("site_width"));
        detailConfig.remove("site_width");
        detailMap.put("source_html", detailConfig.get("source_html"));
        detailConfig.remove("source_html");

        //Map中键中含有数据库带有的下划线，要转为驼峰
        Map detailMapNew = new HashMap();
        Map detailConfigNew = new HashMap();
        Iterator it = detailMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry =(Map.Entry) it.next();
            String s = entry.getKey().toString();
            String key = HumpToLine.lineToHump(s);
            Object value = entry.getValue();
            detailMapNew.put(key,value);
        }
        Iterator it1 = detailConfig.entrySet().iterator();

            while (it1.hasNext()) {
                Map.Entry entry =(Map.Entry) it1.next();
                String s = entry.getKey().toString();
                String key = HumpToLine.lineToHump(s);
                Object value = entry.getValue();
                detailConfigNew.put(key,value);
            }

            if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(detailMapNew.get("clientType"))) {
            webClient.set(webClientPool.getFromPool(Integer.parseInt(detailMapNew.get("needProxy").toString()) == 1 ? true : false));
            WebRequest webRequest = PageParserHelper.getWebRequest(detailUrl, HttpMethod.GET);
            HtmlPage htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, false);
            if (Optional.ofNullable(htmlPage).isPresent()) {
                webClientDetailParser.htmlPageParser(detailConfigNew, htmlPage, detailMapNew, detailData, webName);
            }
        }

        if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(detailMapNew.get("clientType"))) {
            webDriverThreadLocal.set(webDriverFactory.get());
            webDriverThreadLocal.get().get(detailUrl);
            Thread.sleep(3000);
            webDriverDetailParser.webDriverParser(detailConfigNew, detailMapNew, detailData, webName, webDriverThreadLocal.get());
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

}
