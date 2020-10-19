package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * 列表详情表实例
 * 
 * @author 刁烽
 * 
 * @Time 2019-03-26
 *
 * 
 * 
 */
@Deprecated
@Entity
@Table(name = "list_detail") // 表名
public class ListDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer listId;// 列表唯一标识符

	private Integer taskId;// 任务唯一标识符

	private String listTemplateUrl;// 第二页及以后列表规则模板url

	private Integer listPageNumber;// 文章列表总页数

	private String listXpath;// 列表网页的xpath

	private String listClientType;// 列表请求工具，webclient或webdrive

	private String listRequestType;// 列表http请求类型，get或post

	private String listRequestParams;// 列表请求参数，存储为json字符串

	private String listPageHeader;//列表请求头部信息，存储为json字符串

	private int listNeedProxy;// 列表请求是否需要代理，0-无效 1-有效

	private String listResponseType;// 列表http返回类型，html或xml或yml或json

	private String listJsonField;// 如果列表返回类型为json，文章url所在字段的key值

	private int listRepeat;// 是否重复抓取列表数据，0-否 1-是

	private String listPageName;//post请求的page翻页参数名

	private String lastCrawlerUrl;//最后一篇爬取的文章

	private String listJsonKey;//json的key的关系，用,隔开，最后一个key为field数组

	private String listJsonSubstring;//返回是含json的字符时，需截取json，开始字符和截止字符用,隔开

	private String listIdUrl;//返回字段为url的id时，需要自己拼文章url

	private String jsonIdKey;//返回json为id时的字段名

	private String docHost; //文章所在的域名

	private String listYearPage;//列表页有按照年来分目录，传入此参数和listPageNumber一同控制翻页

	private String listDriverPageJs; //webdriver请求时用js控制翻页

	private Integer pageUrlCount;// 每一页文章标题数统计

	private Integer listHref;//针对不规范的href的driver点击

	private String  docHref;//详情页的url（当详情页url与列表页不同时）

	public String getDocHref() {
		return docHref;
	}

	public void setDocHref(String docHref) {
		this.docHref = docHref;
	}

	public Integer getListHref() {
		return listHref;
	}

	public void setListHref(Integer listHref) {
		this.listHref = listHref;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getListTemplateUrl() {
		return listTemplateUrl;
	}

	public void setListTemplateUrl(String listTemplateUrl) {
		this.listTemplateUrl = listTemplateUrl;
	}

	public Integer getListPageNumber() {
		return listPageNumber;
	}

	public void setListPageNumber(Integer listPageNumber) {
		this.listPageNumber = listPageNumber;
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

	public int getListRepeat() {
		return listRepeat;
	}

	public void setListRepeat(int listRepeat) {
		this.listRepeat = listRepeat;
	}

	public String getListPageName() {
		return listPageName;
	}

	public void setListPageName(String listPageName) {
		this.listPageName = listPageName;
	}

	public String getLastCrawlerUrl() {
		return lastCrawlerUrl;
	}

	public void setLastCrawlerUrl(String lastCrawlerUrl) {
		this.lastCrawlerUrl = lastCrawlerUrl;
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

	public String getListYearPage() {
		return listYearPage;
	}

	public void setListYearPage(String listYearPage) {
		this.listYearPage = listYearPage;
	}

	public String getListPageHeader() {
		return listPageHeader;
	}

	public void setListPageHeader(String listPageHeader) {
		this.listPageHeader = listPageHeader;
	}

	public String getListDriverPageJs() {
		return listDriverPageJs;
	}

	public void setListDriverPageJs(String listDriverPageJs) {
		this.listDriverPageJs = listDriverPageJs;
	}

	public Integer getPageUrlCount() {
		return pageUrlCount;
	}

	public void setPageUrlCount(Integer pageUrlCount) {
		this.pageUrlCount = pageUrlCount;
	}

}
