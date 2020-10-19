package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author sunjunjie
 * @date 2020/9/1 15:25
 */
@Entity
public class WarningListEntity {

    private int templateId ;
    private String  listNetworkCount;
    private String  listUrlCount;
    private String  listTemplateCount;
    private String  docNetworkCount;
    private String  docUrlCount;
    private String  docTemplateCount;
    private String  userName;
    private int  isDeal;
    private String dealTime;

    @Id
    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getListNetworkCount() {
        return listNetworkCount;
    }

    public void setListNetworkCount(String listNetworkCount) {
        this.listNetworkCount = listNetworkCount;
    }

    public String getListUrlCount() {
        return listUrlCount;
    }

    public void setListUrlCount(String listUrlCount) {
        this.listUrlCount = listUrlCount;
    }

    public String getListTemplateCount() {
        return listTemplateCount;
    }

    public void setListTemplateCount(String listTemplateCount) {
        this.listTemplateCount = listTemplateCount;
    }

    public String getDocNetworkCount() {
        return docNetworkCount;
    }

    public void setDocNetworkCount(String docNetworkCount) {
        this.docNetworkCount = docNetworkCount;
    }

    public String getDocUrlCount() {
        return docUrlCount;
    }

    public void setDocUrlCount(String docUrlCount) {
        this.docUrlCount = docUrlCount;
    }

    public String getDocTemplateCount() {
        return docTemplateCount;
    }

    public void setDocTemplateCount(String docTemplateCount) {
        this.docTemplateCount = docTemplateCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(int isDeal) {
        this.isDeal = isDeal;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }
}
