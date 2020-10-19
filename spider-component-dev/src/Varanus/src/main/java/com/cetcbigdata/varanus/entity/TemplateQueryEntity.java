package com.cetcbigdata.varanus.entity;

/**
 * @author sunjunjie
 * @date 2020/8/24 10:43
 */
public class TemplateQueryEntity {

    private String projectName;

    private int templateId;

    private String isRun;

    private String webName;

    private String sectionTitle;

    private int getUserId;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getIsRun() {
        return isRun;
    }

    public void setIsRun(String isRun) {
        this.isRun = isRun;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public int getGetUserId() {
        return getUserId;
    }

    public void setGetUserId(int getUserId) {
        this.getUserId = getUserId;
    }
}
