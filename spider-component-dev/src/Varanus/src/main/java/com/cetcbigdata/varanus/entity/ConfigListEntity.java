package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/8/19 13:49
 */
@Entity
@Table(name = "config_list", schema = "data_collection_center", catalog = "")
public class ConfigListEntity {
    private int id;//列表唯一标识符
    private int templateId;//模板id
    private String listXpath;//列表网页的xpath
    private String listClientType;//列表请求工具，webclient或webdriver
    private String listRequestType;//列表http请求类型，get或post
    private String listPageHeader;//列表请求头部信息，存储为json字符串
    private String listRequestParams;//列表请求参数，存储为json字符串
    private int listNeedProxy;//列表请求是否需要代理，0-无效 1-有效
    private String listResponseType;//列表http返回类型，html或xml或yml或json
    private String listJsonField;//如果列表返回类型为json，文章url所在字段的key值
    private int listRepeat;//是否重复抓取列表数据，0-否 1-是
    private String listPageName;//post请求的page翻页参数名
    private String lastCrawlerUrl;//最后一篇爬取的文章
    private String listJsonKey;//json的key的关系，用,隔开，最后一个key为field数组
    private String listJsonSubstring;//返回是含json的字符时，需截取json，开始字符和截止字符用,隔开
    private String listIdUrl;//返回字段为url的id时，需要自己拼文章url
    private String jsonIdKey;//返回json为id时的字段名
    private String docHost;//文章所在域名
    private String listYearPage;//列表页有按照年来分目录，传入此参数和list_page_number一同控制翻页
    private String listDriverPageJs;//webdriver请求时用js控制翻页
    private int pageUrlCount;//每一页文章标题数统计
    private int listHref;//针对不规范的href的driver点击,0-不使用，1-使用，默认不使用
    private String docHref;//文章详情页url的前缀
    private int analyzeType;//列表页面配置解析方式，0-配置方式，1-接口方式，默认为0
    private int interfaceId;//接口id

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "template_id")
    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Basic
    @Column(name = "list_xpath")
    public String getListXpath() {
        return listXpath;
    }

    public void setListXpath(String listXpath) {
        this.listXpath = listXpath;
    }

    @Basic
    @Column(name = "list_client_type")
    public String getListClientType() {
        return listClientType;
    }

    public void setListClientType(String listClientType) {
        this.listClientType = listClientType;
    }

    @Basic
    @Column(name = "list_request_type")
    public String getListRequestType() {
        return listRequestType;
    }

    public void setListRequestType(String listRequestType) {
        this.listRequestType = listRequestType;
    }

    @Basic
    @Column(name = "list_page_header")
    public String getListPageHeader() {
        return listPageHeader;
    }

    public void setListPageHeader(String listPageHeader) {
        this.listPageHeader = listPageHeader;
    }

    @Basic
    @Column(name = "list_request_params")
    public String getListRequestParams() {
        return listRequestParams;
    }

    public void setListRequestParams(String listRequestParams) {
        this.listRequestParams = listRequestParams;
    }

    @Basic
    @Column(name = "list_need_proxy")
    public int getListNeedProxy() {
        return listNeedProxy;
    }

    public void setListNeedProxy(int listNeedProxy) {
        this.listNeedProxy = listNeedProxy;
    }

    @Basic
    @Column(name = "list_response_type")
    public String getListResponseType() {
        return listResponseType;
    }

    public void setListResponseType(String listResponseType) {
        this.listResponseType = listResponseType;
    }

    @Basic
    @Column(name = "list_json_field")
    public String getListJsonField() {
        return listJsonField;
    }

    public void setListJsonField(String listJsonField) {
        this.listJsonField = listJsonField;
    }

    @Basic
    @Column(name = "list_repeat")
    public int getListRepeat() {
        return listRepeat;
    }

    public void setListRepeat(int listRepeat) {
        this.listRepeat = listRepeat;
    }

    @Basic
    @Column(name = "list_page_name")
    public String getListPageName() {
        return listPageName;
    }

    public void setListPageName(String listPageName) {
        this.listPageName = listPageName;
    }

    @Basic
    @Column(name = "last_crawler_url")
    public String getLastCrawlerUrl() {
        return lastCrawlerUrl;
    }

    public void setLastCrawlerUrl(String lastCrawlerUrl) {
        this.lastCrawlerUrl = lastCrawlerUrl;
    }

    @Basic
    @Column(name = "list_json_key")
    public String getListJsonKey() {
        return listJsonKey;
    }

    public void setListJsonKey(String listJsonKey) {
        this.listJsonKey = listJsonKey;
    }

    @Basic
    @Column(name = "list_json_substring")
    public String getListJsonSubstring() {
        return listJsonSubstring;
    }

    public void setListJsonSubstring(String listJsonSubstring) {
        this.listJsonSubstring = listJsonSubstring;
    }

    @Basic
    @Column(name = "list_id_url")
    public String getListIdUrl() {
        return listIdUrl;
    }

    public void setListIdUrl(String listIdUrl) {
        this.listIdUrl = listIdUrl;
    }

    @Basic
    @Column(name = "json_id_key")
    public String getJsonIdKey() {
        return jsonIdKey;
    }

    public void setJsonIdKey(String jsonIdKey) {
        this.jsonIdKey = jsonIdKey;
    }

    @Basic
    @Column(name = "doc_host")
    public String getDocHost() {
        return docHost;
    }

    public void setDocHost(String docHost) {
        this.docHost = docHost;
    }

    @Basic
    @Column(name = "list_year_page")
    public String getListYearPage() {
        return listYearPage;
    }

    public void setListYearPage(String listYearPage) {
        this.listYearPage = listYearPage;
    }

    @Basic
    @Column(name = "list_driver_page_js")
    public String getListDriverPageJs() {
        return listDriverPageJs;
    }

    public void setListDriverPageJs(String listDriverPageJs) {
        this.listDriverPageJs = listDriverPageJs;
    }

    @Basic
    @Column(name = "page_url_count")
    public int getPageUrlCount() {
        return pageUrlCount;
    }

    public void setPageUrlCount(int pageUrlCount) {
        this.pageUrlCount = pageUrlCount;
    }

    @Basic
    @Column(name = "list_href")
    public int getListHref() {
        return listHref;
    }

    public void setListHref(int listHref) {
        this.listHref = listHref;
    }

    @Basic
    @Column(name = "doc_href")
    public String getDocHref() {
        return docHref;
    }

    public void setDocHref(String docHref) {
        this.docHref = docHref;
    }

    @Basic
    @Column(name = "analyze_type")
    public int getAnalyzeType() {
        return analyzeType;
    }

    public void setAnalyzeType(int analyzeType) {
        this.analyzeType = analyzeType;
    }

    @Basic
    @Column(name = "interface_id")
    public int getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(int interfaceId) {
        this.interfaceId = interfaceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigListEntity entity = (ConfigListEntity) o;
        return id == entity.id &&
                listNeedProxy == entity.listNeedProxy &&
                analyzeType == entity.analyzeType &&
                Objects.equals(templateId, entity.templateId) &&
                Objects.equals(listXpath, entity.listXpath) &&
                Objects.equals(listClientType, entity.listClientType) &&
                Objects.equals(listRequestType, entity.listRequestType) &&
                Objects.equals(listPageHeader, entity.listPageHeader) &&
                Objects.equals(listRequestParams, entity.listRequestParams) &&
                Objects.equals(listResponseType, entity.listResponseType) &&
                Objects.equals(listJsonField, entity.listJsonField) &&
                Objects.equals(listRepeat, entity.listRepeat) &&
                Objects.equals(listPageName, entity.listPageName) &&
                Objects.equals(lastCrawlerUrl, entity.lastCrawlerUrl) &&
                Objects.equals(listJsonKey, entity.listJsonKey) &&
                Objects.equals(listJsonSubstring, entity.listJsonSubstring) &&
                Objects.equals(listIdUrl, entity.listIdUrl) &&
                Objects.equals(jsonIdKey, entity.jsonIdKey) &&
                Objects.equals(docHost, entity.docHost) &&
                Objects.equals(listYearPage, entity.listYearPage) &&
                Objects.equals(listDriverPageJs, entity.listDriverPageJs) &&
                Objects.equals(pageUrlCount, entity.pageUrlCount) &&
                Objects.equals(listHref, entity.listHref) &&
                Objects.equals(docHref, entity.docHref) &&
                Objects.equals(interfaceId, entity.interfaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, templateId, listXpath, listClientType, listRequestType, listPageHeader, listRequestParams, listNeedProxy, listResponseType, listJsonField, listRepeat, listPageName, lastCrawlerUrl, listJsonKey, listJsonSubstring, listIdUrl, jsonIdKey, docHost, listYearPage, listDriverPageJs, pageUrlCount, listHref, docHref, analyzeType, interfaceId);
    }
}
