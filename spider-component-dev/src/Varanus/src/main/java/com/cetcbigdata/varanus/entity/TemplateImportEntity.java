package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author sunjunjie
 * @date 2020/8/24 16:38
 */
@Entity
public class TemplateImportEntity {

    private int id;

    private String webName;

    private String sectionTitle;

    private String sectionUrl;

    private String listXpath;

    @Id
    public int getId() {
        return id;
    }

    public void setId(int Id) {
        this.id = id;
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

    public String getSectionUrl() {
        return sectionUrl;
    }

    public void setSectionUrl(String sectionUrl) {
        this.sectionUrl = sectionUrl;
    }

    public String getListXpath() {
        return listXpath;
    }

    public void setListXpath(String listXpath) {
        this.listXpath = listXpath;
    }
}
