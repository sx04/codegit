package com.cetcbigdata.varanus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Deprecated
@Entity
public class ListCrawlerdataCount implements Serializable {

    @Id
    @Column(length = 32)
    private Integer taskId;
    private String responsiblePeople;
    private String department;
    private String sectionTitle;
    private String sectionUrl;// 板块url
    private Integer listPageNumber;// 文章列表总页数
    private Integer pageUrlCount;
    private Integer sectionDocCount;// 文档插入时间

    private String listXpath;// 列表网页的xpath

    private String listClientType;// 列表请求工具，webclient或webdrive

    private String listRequestType;// 列表http请求类型，get或post

    private String listRequestParams;// 列表请求参数，存储为json字符串

    private String listPageHeader;//列表请求头部信息，存储为json字符串

    private int listNeedProxy;// 列表请求是否需要代理，0-无效 1-有效

    private String listResponseType;// 列表http返回类型，html或xml或yml或json

    private String listJsonField;// 如果列表返回类型为json，文章url所在字段的key值

    private String listPageName;// post请求的page翻页参数名

    private String listJsonKey;// json的key的关系，用,隔开，最后一个key为field数组

    private String listJsonSubstring;// 返回是含json的字符时，需截取json，开始字符和截止字符用,隔开

    private String listIdUrl;// 返回字段为url的id时，需要自己拼文章url

    private String jsonIdKey;// 返回json为id时的字段名

    private String docHost; // 文章所在的域名



    @Transient
    private Integer webUrlCount;
    @Transient
    private Integer differenceUrlCount;
    @Transient
    private String successRate;

//    public ListCrawlerdataCount() {
//    }

//    public ListCrawlerdataCount(String responsiblePeople, Integer taskId,String department, String sectionTitle, String sectionUrl, Integer listPageNumber,
//                                Integer pageUrlCount, Integer sectionDocCount,Integer webUrlCount, Integer differenceUrlCount,String successRate) {
//        this.taskId = taskId;
//        this.department = department;
//        this.responsiblePeople = responsiblePeople;
//        this.sectionTitle = sectionTitle;
//        this.sectionUrl = sectionUrl;
//        this.listPageNumber = listPageNumber;
//        this.pageUrlCount = pageUrlCount;
//        this.sectionDocCount = sectionDocCount;
//        this.webUrlCount = webUrlCount;
//        this.differenceUrlCount = differenceUrlCount;
//        this.successRate = successRate;
//
//    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getResponsiblePeople() {
        return responsiblePeople;
    }

    public void setResponsiblePeople(String responsiblePeople) {
        this.responsiblePeople = responsiblePeople;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionUrl() {
        return sectionUrl;
    }

    public void setSectionUrl(String sectionUrl) {
        this.sectionUrl = sectionUrl;
    }

    public Integer getListPageNumber() {
        return listPageNumber;
    }

    public void setListPageNumber(Integer listPageNumber) {
        this.listPageNumber = listPageNumber;
    }

    public Integer getPageUrlCount() {
        return pageUrlCount;
    }

    public void setPageUrlCount(Integer pageUrlCount) {
        this.pageUrlCount = pageUrlCount;
    }

    public Integer getWebUrlCount() {
        return webUrlCount;
    }

    public void setWebUrlCount(Integer webUrlCount) {
        this.webUrlCount = webUrlCount;
    }

    public Integer getSectionDocCount() {
        return sectionDocCount;
    }

    public void setSectionDocCount(Integer sectionDocCount) {
        this.sectionDocCount = sectionDocCount;
    }

    public Integer getDifferenceUrlCount() {
        return differenceUrlCount;
    }

    public void setDifferenceUrlCount(Integer differenceUrlCount) {
        this.differenceUrlCount = differenceUrlCount;
    }

    public String getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(String successRate) {
        this.successRate = successRate;
    }

    public String getListXpath() {
        return listXpath;
    }

    public void setListXpath(String listXpath) {
        this.listXpath = listXpath;
    }

    public String getListClientType() {
        return listClientType;
    }

    public void setListClientType(String listClientType) {
        this.listClientType = listClientType;
    }

    public String getListRequestType() {
        return listRequestType;
    }

    public void setListRequestType(String listRequestType) {
        this.listRequestType = listRequestType;
    }

    public String getListRequestParams() {
        return listRequestParams;
    }

    public void setListRequestParams(String listRequestParams) {
        this.listRequestParams = listRequestParams;
    }

    public String getListPageHeader() {
        return listPageHeader;
    }

    public void setListPageHeader(String listPageHeader) {
        this.listPageHeader = listPageHeader;
    }

    public int getListNeedProxy() {
        return listNeedProxy;
    }

    public void setListNeedProxy(int listNeedProxy) {
        this.listNeedProxy = listNeedProxy;
    }

    public String getListResponseType() {
        return listResponseType;
    }

    public void setListResponseType(String listResponseType) {
        this.listResponseType = listResponseType;
    }

    public String getListJsonField() {
        return listJsonField;
    }

    public void setListJsonField(String listJsonField) {
        this.listJsonField = listJsonField;
    }

    public String getListPageName() {
        return listPageName;
    }

    public void setListPageName(String listPageName) {
        this.listPageName = listPageName;
    }

    public String getListJsonKey() {
        return listJsonKey;
    }

    public void setListJsonKey(String listJsonKey) {
        this.listJsonKey = listJsonKey;
    }

    public String getListJsonSubstring() {
        return listJsonSubstring;
    }

    public void setListJsonSubstring(String listJsonSubstring) {
        this.listJsonSubstring = listJsonSubstring;
    }

    public String getListIdUrl() {
        return listIdUrl;
    }

    public void setListIdUrl(String listIdUrl) {
        this.listIdUrl = listIdUrl;
    }

    public String getJsonIdKey() {
        return jsonIdKey;
    }

    public void setJsonIdKey(String jsonIdKey) {
        this.jsonIdKey = jsonIdKey;
    }

    public String getDocHost() {
        return docHost;
    }

    public void setDocHost(String docHost) {
        this.docHost = docHost;
    }
}