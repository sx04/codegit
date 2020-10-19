package com.cetcbigdata.varanus.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**

 * 列表详情表实例

 * @author 宋旻雨

 * @Time 2019-03-26

 *

 */
@Deprecated
@Entity
@Table(name = "doc_detail")//表名
public class DocDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer docId;//文章唯一标识符

    private Integer listId;//列表唯一标识符

    private Integer taskId;//任务唯一标识符

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

    @Transient
    private String testUrl;//测试专用url

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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
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

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getInfoBoxXpath() {
        return infoBoxXpath;
    }

    public void setInfoBoxXpath(String infoBoxXpath) {
        this.infoBoxXpath = infoBoxXpath;
    }
}


