package com.cetcbigdata.varanus.entity;

import javax.persistence.Id;

/**
 * @author sunjunjie
 * @date 2020/9/10 15:06
 */
public class TaskWarningListEntity {
    private int id;//报警id
    private int templateId;//列表id
    private String listWarningType;//列表配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败
    private String listWarningMsg;//列表配置报警描述
    private String detailWarningType;//文章详情配置报警类型 0-网络异常 1-网址访问失败 2-模板匹配失败
    private String detailWarningMsg;//文章详情配置报警描述
    private String warningTime;//报警时间
    private int isDeal;//错误是否处理，0-未处理，1-处理，默认未处理
    private Integer dealUserId;//错误处理人id
    private String dealTime;//错误处理时间

    @Id
    public int getId() {
        return id;
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

    public String getListWarningType() {
        return listWarningType;
    }

    public void setListWarningType(String listWarningType) {
        this.listWarningType = listWarningType;
    }

    public String getListWarningMsg() {
        return listWarningMsg;
    }

    public void setListWarningMsg(String listWarningMsg) {
        this.listWarningMsg = listWarningMsg;
    }

    public String getDetailWarningType() {
        return detailWarningType;
    }

    public void setDetailWarningType(String detailWarningType) {
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
