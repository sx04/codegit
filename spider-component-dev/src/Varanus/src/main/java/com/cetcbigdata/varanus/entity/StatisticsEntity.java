package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author sunjunjie
 * @date 2020/8/31 16:20
 */
@Entity
public class StatisticsEntity {

    private String id;
    private String webName;
    private String sectionTitle;
    private String title;
    private String insertDate;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }
}
