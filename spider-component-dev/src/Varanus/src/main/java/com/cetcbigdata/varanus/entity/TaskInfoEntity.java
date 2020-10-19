package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/9/16 11:11
 */
@Entity
@Table(name = "task_info", schema = "data_collection_center", catalog = "")
public class TaskInfoEntity {
    private int id;
    private String projectId;
    private String webName;
    private String sectionTitle;
    private String sectionUrl;
    private Timestamp insertTime;
    private Timestamp updateTime;
    private int isGet;
    private Integer getUserId;
    private String domainCode;
    private String srcTypeCode;
    private int state;
    private Timestamp stateUpdateTime;
    private String tableInfoText;
    private byte[] tableInfoImag;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "project_id")
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Basic
    @Column(name = "web_name")
    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    @Basic
    @Column(name = "section_title")
    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    @Basic
    @Column(name = "section_url")
    public String getSectionUrl() {
        return sectionUrl;
    }

    public void setSectionUrl(String sectionUrl) {
        this.sectionUrl = sectionUrl;
    }

    @Basic
    @Column(name = "insert_time")
    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "update_time")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "is_get")
    public int getIsGet() {
        return isGet;
    }

    public void setIsGet(int isGet) {
        this.isGet = isGet;
    }

    @Basic
    @Column(name = "get_user_id")
    public Integer getGetUserId() {
        return getUserId;
    }

    public void setGetUserId(Integer getUserId) {
        this.getUserId = getUserId;
    }

    @Basic
    @Column(name = "domain_code")
    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @Basic
    @Column(name = "src_type_code")
    public String getSrcTypeCode() {
        return srcTypeCode;
    }

    public void setSrcTypeCode(String srcTypeCode) {
        this.srcTypeCode = srcTypeCode;
    }

    @Basic
    @Column(name = "state")
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Basic
    @Column(name = "state_update_time")
    public Timestamp getStateUpdateTime() {
        return stateUpdateTime;
    }

    public void setStateUpdateTime(Timestamp stateUpdateTime) {
        this.stateUpdateTime = stateUpdateTime;
    }

    @Basic
    @Column(name = "table_info_text")
    public String getTableInfoText() {
        return tableInfoText;
    }

    public void setTableInfoText(String tableInfoText) {
        this.tableInfoText = tableInfoText;
    }

    @Basic
    @Column(name = "table_info_imag")
    public byte[] getTableInfoImag() {
        return tableInfoImag;
    }

    public void setTableInfoImag(byte[] tableInfoImag) {
        this.tableInfoImag = tableInfoImag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInfoEntity that = (TaskInfoEntity) o;
        return id == that.id &&
                isGet == that.isGet &&
                state == that.state &&
                Objects.equals(projectId, that.projectId) &&
                Objects.equals(webName, that.webName) &&
                Objects.equals(sectionTitle, that.sectionTitle) &&
                Objects.equals(sectionUrl, that.sectionUrl) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(getUserId, that.getUserId) &&
                Objects.equals(domainCode, that.domainCode) &&
                Objects.equals(srcTypeCode, that.srcTypeCode) &&
                Objects.equals(stateUpdateTime, that.stateUpdateTime) &&
                Objects.equals(tableInfoText, that.tableInfoText) &&
                Arrays.equals(tableInfoImag, that.tableInfoImag);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, projectId, webName, sectionTitle, sectionUrl, insertTime, updateTime, isGet, getUserId, domainCode, srcTypeCode, state, stateUpdateTime, tableInfoText);
        result = 31 * result + Arrays.hashCode(tableInfoImag);
        return result;
    }
}
