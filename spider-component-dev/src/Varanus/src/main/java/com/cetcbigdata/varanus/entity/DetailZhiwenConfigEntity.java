package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;
@Deprecated
@Entity
@Table(name = "detail_zhiwen_config", schema = "data_collection_center", catalog = "")
public class DetailZhiwenConfigEntity {
    private int id;
    private int listId;
    private String clientType;
    private String requestType;
    private String requestParams;
    private String responseType;
    private String jsonField;
    private Integer needProxy;
    private String testUrl;
    private int analyzeType;
    private Integer interfaceId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "list_id")
    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @Basic
    @Column(name = "client_type")
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
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
    public int getAnalyzeType() {
        return analyzeType;
    }

    public void setAnalyzeType(int analyzeType) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailZhiwenConfigEntity that = (DetailZhiwenConfigEntity) o;
        return id == that.id &&
                listId == that.listId &&
                analyzeType == that.analyzeType &&
                Objects.equals(clientType, that.clientType) &&
                Objects.equals(requestType, that.requestType) &&
                Objects.equals(requestParams, that.requestParams) &&
                Objects.equals(responseType, that.responseType) &&
                Objects.equals(jsonField, that.jsonField) &&
                Objects.equals(needProxy, that.needProxy) &&
                Objects.equals(testUrl, that.testUrl) &&
                Objects.equals(interfaceId, that.interfaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, listId, clientType, requestType, requestParams, responseType, jsonField, needProxy, testUrl, analyzeType, interfaceId);
    }
}
