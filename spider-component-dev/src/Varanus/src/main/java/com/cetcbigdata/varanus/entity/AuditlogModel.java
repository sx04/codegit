package com.cetcbigdata.varanus.entity;


import java.util.Date;

/**
 * @author sunjunjie
 * @date 2020/9/7 14:42
 */
public class AuditlogModel  {

    private String userId;
    private String operateType;
    private String startTime;
    private String endTime;
    private String operateModule;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOperateModule() {
        return operateModule;
    }

    public void setOperateModule(String operateModule) {
        this.operateModule = operateModule;
    }
}
