package com.cetcbigdata.varanus.entity;

/**
 * @author sunjunjie
 * @date 2020/8/18 13:54
 */
public class TemplateBasicEntity {

    private int id;

    private String templateUrl;

    private int pageNumber;

    private int docNumber;

    private int isRun;

    private int taskId;

    private String domainCode;

    private String srcTypeCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(int docNumber) {
        this.docNumber = docNumber;
    }

    public int getIsRun() {
        return isRun;
    }

    public void setIsRun(int isRun) {
        this.isRun = isRun;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getSrcTypeCode() {
        return srcTypeCode;
    }

    public void setSrcTypeCode(String srcTypeCode) {
        this.srcTypeCode = srcTypeCode;
    }
}
