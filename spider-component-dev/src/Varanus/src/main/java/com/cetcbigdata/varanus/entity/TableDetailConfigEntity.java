package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:15
 */
@Entity
@Table(name = "table_detail_config", schema = "data_collection_center", catalog = "")
public class TableDetailConfigEntity {
    private int id;
    private int taskId;
    private String requestType;
    private String requestParams;
    private String responseType;
    private String jsonField;
    private Integer needProxy;
    private String testUrl;
    private Integer analyzeType;
    private Integer interfaceId;
    private String tableXpath;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "task_id")
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Basic
    @Column(name = "request_type")
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Basic
    @Column(name = "request_params")
    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    @Basic
    @Column(name = "response_type")
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Basic
    @Column(name = "json_field")
    public String getJsonField() {
        return jsonField;
    }

    public void setJsonField(String jsonField) {
        this.jsonField = jsonField;
    }

    @Basic
    @Column(name = "need_proxy")
    public Integer getNeedProxy() {
        return needProxy;
    }

    public void setNeedProxy(Integer needProxy) {
        this.needProxy = needProxy;
    }

    @Basic
    @Column(name = "test_url")
    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    @Basic
    @Column(name = "analyze_type")
    public Integer getAnalyzeType() {
        return analyzeType;
    }

    public void setAnalyzeType(Integer analyzeType) {
        this.analyzeType = analyzeType;
    }

    @Basic
    @Column(name = "interface_id")
    public Integer getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Integer interfaceId) {
        this.interfaceId = interfaceId;
    }

    @Basic
    @Column(name = "table_xpath")
    public String getTableXpath() {
        return tableXpath;
    }

    public void setTableXpath(String tableXpath) {
        this.tableXpath = tableXpath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableDetailConfigEntity that = (TableDetailConfigEntity) o;
        return id == that.id &&
                taskId == that.taskId &&
                Objects.equals(requestType, that.requestType) &&
                Objects.equals(requestParams, that.requestParams) &&
                Objects.equals(responseType, that.responseType) &&
                Objects.equals(jsonField, that.jsonField) &&
                Objects.equals(needProxy, that.needProxy) &&
                Objects.equals(testUrl, that.testUrl) &&
                Objects.equals(analyzeType, that.analyzeType) &&
                Objects.equals(interfaceId, that.interfaceId) &&
                Objects.equals(tableXpath, that.tableXpath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, requestType, requestParams, responseType, jsonField, needProxy, testUrl, analyzeType, interfaceId, tableXpath);
    }
}
