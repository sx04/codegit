package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class TaskWarningEntity {
    private int id;//报警id
    private int templateId;//模板id
    private Integer listWarningType;//列表配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败
    private String listWarningMsg;//列表配置报警描述
    private Integer detailWarningType;//文章详情配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败
    private String detailWarningMsg;//文章详情配置报警描述
    private String warningTime;//报警时间
    private int isDeal;//错误是否处理，0-未处理，1-处理，默认未处理
    private Integer dealUserId;//错误处理人id
    private String dealTime;//错误处理时间

    @Id
    public int getId() {
        return id;
    }

    public TaskWarningEntity(int templateId, Integer listWarningType, String listWarningMsg) {
        this.templateId = templateId;
        this.listWarningType = listWarningType;
        this.listWarningMsg = listWarningMsg;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public Integer getListWarningType() {
        return listWarningType;
    }

    public void setListWarningType(Integer listWarningType) {
        this.listWarningType = listWarningType;
    }

    public String getListWarningMsg() {
        return listWarningMsg;
    }

    public void setListWarningMsg(String listWarningMsg) {
        this.listWarningMsg = listWarningMsg;
    }

    public Integer getDetailWarningType() {
        return detailWarningType;
    }

    public void setDetailWarningType(Integer detailWarningType) {
        this.detailWarningType = detailWarningType;
    }

    public String getDetailWarningMsg() {
        return detailWarningMsg;
    }

    public void setDetailWarningMsg(String detailWarningMsg) {
        this.detailWarningMsg = detailWarningMsg;
    }

    public String getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(String warningTime) {
        this.warningTime = warningTime;
    }

    public int getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(int isDeal) {
        this.isDeal = isDeal;
    }

    public Integer getDealUserId() {
        return dealUserId;
    }

    public void setDealUserId(Integer dealUserId) {
        this.dealUserId = dealUserId;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }
}
