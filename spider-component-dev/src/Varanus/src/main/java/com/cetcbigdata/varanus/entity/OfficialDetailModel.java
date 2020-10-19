package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Deprecated
@Entity
public class OfficialDetailModel {
    @Id
    private Integer docId;//文章唯一标识符

    private Integer listId;//列表唯一标识符

    private String docClientType;//文章请求工具，webclient或webdrive

    private String docRequestType;//文章http请求类型，get或post

    private String docRequestParams;//文章请求参数，存储为json字符

    private String docResponseType;//文章http返回类型，html或xml或yml或json

    private String docJsonField;//如果文章返回类型为json，文章url所在字段的key值

    private int docNeedProxy;//文章请求是否需要代理，0-无效 1-有效

    private String sourceXpath;//文章来源xpath

    private String titleXpath;//文章标题xpath

    private String infoBoxXpath; //信息盒区域div源码

    private String contentXpath;//文章正文xpath

    private String policyDateXpath;//政策日期xpath

    private String publishOfficeXpath;//发文机关xpath

    private Integer siteWidth;//网站宽度

    private String publishDateXpath;//发布日期xpath

    private String draftDateXpath;//成文日期xpath

    private String attachmentXpath;//附件xpath

    private String imageXpath;//图片xpath

    private String indexNumberXpath;//索引号xpath

    private String topicTypeXpath;//主题分类xpath

    private String referenceNumberXpath;//发文字号xpath

    private String topicWordsXpath;//主题词xpath

    private String summaryXpath;//内容概述xpath

    private String formCodeXpath;//形式代码xpath

    private String themeXpath;//体裁xpath

    private String effectiveDateXpath;//生效日期xpath

    private String expireDateXpath;//失效日期xpath

    private String imageBasepath;//图片基本路径

    private String attachmentBasepath;//附件基本路径

    private Integer taskId;// 任务唯一标识符

    private String sqlTable;// MySQL存储表名

    private String department;// 文章发布部门

    private String area;// 文章发布部门所属行政区域

    private String sectionTitle;// 任务板块标题

    private String sectionUrl;// 板块url

    private int isValid;// 配置是否有效，0-无效 1-有效

    private Date insertTime;// 文章插入时间

    private Date updateDate;// 文章更新时间

    private Boolean urlAvailability;// 板块列表首页url网络是否可达，0-无效 1-有效

    private String siteVersion; // 网站版本

    private String versionExceptionCount; // 网站版本异常次数

    private String responsiblePeople; // 任务负责人

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getDocClientType() {
        return docClientType;
    }

    public void setDocClientType(String docClientType) {
        this.docClientType = docClientType;
    }

    public String getDocRequestType() {
        return docRequestType;
    }

    public void setDocRequestType(String docRequestType) {
        this.docRequestType = docRequestType;
    }

    public String getDocRequestParams() {
        return docRequestParams;
    }

    public void setDocRequestParams(String docRequestParams) {
        this.docRequestParams = docRequestParams;
    }

    public String getDocResponseType() {
        return docResponseType;
    }

    public void setDocResponseType(String docResponseType) {
        this.docResponseType = docResponseType;
    }

    public String getDocJsonField() {
        return docJsonField;
    }

    public void setDocJsonField(String docJsonField) {
        this.docJsonField = docJsonField;
    }

    public int getDocNeedProxy() {
        return docNeedProxy;
    }

    public void setDocNeedProxy(int docNeedProxy) {
        this.docNeedProxy = docNeedProxy;
    }

    public String getSourceXpath() {
        return sourceXpath;
    }

    public void setSourceXpath(String sourceXpath) {
        this.sourceXpath = sourceXpath;
    }

    public String getTitleXpath() {
        return titleXpath;
    }

    public void setTitleXpath(String titleXpath) {
        this.titleXpath = titleXpath;
    }

    public String getInfoBoxXpath() {
        return infoBoxXpath;
    }

    public void setInfoBoxXpath(String infoBoxXpath) {
        this.infoBoxXpath = infoBoxXpath;
    }

    public String getContentXpath() {
        return contentXpath;
    }

    public void setContentXpath(String contentXpath) {
        this.contentXpath = contentXpath;
    }

    public String getPolicyDateXpath() {
        return policyDateXpath;
    }

    public void setPolicyDateXpath(String policyDateXpath) {
        this.policyDateXpath = policyDateXpath;
    }

    public String getPublishOfficeXpath() {
        return publishOfficeXpath;
    }

    public void setPublishOfficeXpath(String publishOfficeXpath) {
        this.publishOfficeXpath = publishOfficeXpath;
    }

    public Integer getSiteWidth() {
        return siteWidth;
    }

    public void setSiteWidth(Integer siteWidth) {
        this.siteWidth = siteWidth;
    }

    public String getPublishDateXpath() {
        return publishDateXpath;
    }

    public void setPublishDateXpath(String publishDateXpath) {
        this.publishDateXpath = publishDateXpath;
    }

    public String getDraftDateXpath() {
        return draftDateXpath;
    }

    public void setDraftDateXpath(String draftDateXpath) {
        this.draftDateXpath = draftDateXpath;
    }

    public String getAttachmentXpath() {
        return attachmentXpath;
    }

    public void setAttachmentXpath(String attachmentXpath) {
        this.attachmentXpath = attachmentXpath;
    }

    public String getImageXpath() {
        return imageXpath;
    }

    public void setImageXpath(String imageXpath) {
        this.imageXpath = imageXpath;
    }

    public String getIndexNumberXpath() {
        return indexNumberXpath;
    }

    public void setIndexNumberXpath(String indexNumberXpath) {
        this.indexNumberXpath = indexNumberXpath;
    }

    public String getTopicTypeXpath() {
        return topicTypeXpath;
    }

    public void setTopicTypeXpath(String topicTypeXpath) {
        this.topicTypeXpath = topicTypeXpath;
    }

    public String getReferenceNumberXpath() {
        return referenceNumberXpath;
    }

    public void setReferenceNumberXpath(String referenceNumberXpath) {
        this.referenceNumberXpath = referenceNumberXpath;
    }

    public String getTopicWordsXpath() {
        return topicWordsXpath;
    }

    public void setTopicWordsXpath(String topicWordsXpath) {
        this.topicWordsXpath = topicWordsXpath;
    }

    public String getSummaryXpath() {
        return summaryXpath;
    }

    public void setSummaryXpath(String summaryXpath) {
        this.summaryXpath = summaryXpath;
    }

    public String getFormCodeXpath() {
        return formCodeXpath;
    }

    public void setFormCodeXpath(String formCodeXpath) {
        this.formCodeXpath = formCodeXpath;
    }

    public String getThemeXpath() {
        return themeXpath;
    }

    public void setThemeXpath(String themeXpath) {
        this.themeXpath = themeXpath;
    }

    public String getEffectiveDateXpath() {
        return effectiveDateXpath;
    }

    public void setEffectiveDateXpath(String effectiveDateXpath) {
        this.effectiveDateXpath = effectiveDateXpath;
    }

    public String getExpireDateXpath() {
        return expireDateXpath;
    }

    public void setExpireDateXpath(String expireDateXpath) {
        this.expireDateXpath = expireDateXpath;
    }

    public String getImageBasepath() {
        return imageBasepath;
    }

    public void setImageBasepath(String imageBasepath) {
        this.imageBasepath = imageBasepath;
    }

    public String getAttachmentBasepath() {
        return attachmentBasepath;
    }

    public void setAttachmentBasepath(String attachmentBasepath) {
        this.attachmentBasepath = attachmentBasepath;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getSqlTable() {
        return sqlTable;
    }

    public void setSqlTable(String sqlTable) {
        this.sqlTable = sqlTable;
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

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getUrlAvailability() {
        return urlAvailability;
    }

    public void setUrlAvailability(Boolean urlAvailability) {
        this.urlAvailability = urlAvailability;
    }

    public String getSiteVersion() {
        return siteVersion;
    }

    public void setSiteVersion(String siteVersion) {
        this.siteVersion = siteVersion;
    }

    public String getVersionExceptionCount() {
        return versionExceptionCount;
    }

    public void setVersionExceptionCount(String versionExceptionCount) {
        this.versionExceptionCount = versionExceptionCount;
    }

    public String getResponsiblePeople() {
        return responsiblePeople;
    }

    public void setResponsiblePeople(String responsiblePeople) {
        this.responsiblePeople = responsiblePeople;
    }
}
