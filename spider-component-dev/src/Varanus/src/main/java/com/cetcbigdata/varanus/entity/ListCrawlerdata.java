package com.cetcbigdata.varanus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Deprecated
@Entity
public class ListCrawlerdata implements Serializable {

    @Id
    @Column(length = 32)
    private String keyId;
    private String department;
    private String title;
    private String pubOfficeInfo;
    private String pubDateInfo;
    private String policyDate;
    private String insertDate;// 文档插入时间

    public ListCrawlerdata() {
    }

    public ListCrawlerdata(String keyId, String department, String title, String pubOfficeInfo, String pubDateInfo, String policyDate, String insertDate) {
        this.keyId = keyId;
        this.department = department;
        this.title = title;
        this.pubOfficeInfo = pubOfficeInfo;
        this.pubDateInfo = pubDateInfo;
        this.policyDate = policyDate;
        this.insertDate = insertDate;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubOfficeInfo() {
        return pubOfficeInfo;
    }

    public void setPubOfficeInfo(String pubOfficeInfo) {
        this.pubOfficeInfo = pubOfficeInfo;
    }

    public String getPubDateInfo() {
        return pubDateInfo;
    }

    public void setPubDateInfo(String pubDateInfo) {
        this.pubDateInfo = pubDateInfo;
    }

    public String getPolicyDate() {
        return policyDate;
    }

    public void setPolicyDate(String policyDate) {
        this.policyDate = policyDate;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }
}