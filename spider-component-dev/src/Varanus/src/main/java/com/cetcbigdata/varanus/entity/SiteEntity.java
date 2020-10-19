package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Deprecated
@Entity
public class SiteEntity implements Serializable {

	@Id
	private Integer taskId;// 任务唯一标识符

	private String department;// 文章发布部门

	private String area;// 文章发布部门所属行政区域

	private String sectionTitle;// 任务板块标题

	private String sectionUrl;// 板块url

	private int isValid;// 配置是否有效，0-无效 1-有效

	private Date updateDate;// 文章更新时间

	private String siteVersion; // 网站版本

	private String responsiblePeople; // 任务负责人

	// 之后为list_detail表字段

	private Integer listId;// 列表唯一标识符

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

	private String listPageName;// post请求的page翻页参数名

	private String listJsonKey;// json的key的关系，用,隔开，最后一个key为field数组

	private String listJsonSubstring;// 返回是含json的字符时，需截取json，开始字符和截止字符用,隔开

	private String listIdUrl;// 返回字段为url的id时，需要自己拼文章url

	private String jsonIdKey;// 返回json为id时的字段名

	private String docHost; // 文章所在的域名

	private String listYearPage;// 列表页有按照年来分目录，传入此参数和listPageNumber一同控制翻页

	private String listDriverPageJs; //webdriver请求时用js控制翻页

	private String domainName;//领域

	private String dataSrcTypeName;//网站数据来源

	private String pageUrlCount;//一页的链接数量

	private Integer listHref;//针对不规范的href的driver点击

	private String  docHref;//详情页的url（当详情页url与列表页不同时）

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getSiteVersion() {
		return siteVersion;
	}

	public void setSiteVersion(String siteVersion) {
		this.siteVersion = siteVersion;
	}

	public String getResponsiblePeople() {
		return responsiblePeople;
	}

	public void setResponsiblePeople(String responsiblePeople) {
		this.responsiblePeople = responsiblePeople;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
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

	public String getListDriverPageJs() {
		return listDriverPageJs;
	}

	public void setListDriverPageJs(String listDriverPageJs) {
		this.listDriverPageJs = listDriverPageJs;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDataSrcTypeName() {
		return dataSrcTypeName;
	}

	public void setDataSrcTypeName(String dataSrcTypeName) {
		this.dataSrcTypeName = dataSrcTypeName;
	}

	public String getPageUrlCount() {
		return pageUrlCount;
	}

	public void setPageUrlCount(String pageUrlCount) {
		this.pageUrlCount = pageUrlCount;
	}

	public Integer getListHref() {
		return listHref;
	}

	public void setListHref(Integer listHref) {
		this.listHref = listHref;
	}

	public String getDocHref() {
		return docHref;
	}

	public void setDocHref(String docHref) {
		this.docHref = docHref;
	}
}
