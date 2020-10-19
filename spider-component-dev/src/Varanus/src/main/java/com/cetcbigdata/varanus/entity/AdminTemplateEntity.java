package com.cetcbigdata.varanus.entity;

import java.sql.Timestamp;

/**
 * @author sunjunjie
 * @date 2020/9/3 14:21
 */
public class AdminTemplateEntity {

    private String name;
    private String webName;
    private String sectionTitle;
    private String userName;
    private String stateUpdateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStateUpdateTime() {
        return stateUpdateTime;
    }

    public void setStateUpdateTime(String stateUpdateTime) {
        this.stateUpdateTime = stateUpdateTime;
    }
}
