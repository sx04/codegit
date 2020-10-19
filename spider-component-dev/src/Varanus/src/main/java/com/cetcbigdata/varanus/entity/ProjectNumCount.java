package com.cetcbigdata.varanus.entity;

import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/9/3 15:37
 */
public class ProjectNumCount {

    private String projectId;

    private String projectName;

    private List projectCharts;

    private int todayTempAdd;

    private int todayDataAdd;

    private int tempNum;

    private int dataNum;

    public List getProjectCharts() {
        return projectCharts;
    }

    public void setProjectCharts(List projectCharts) {
        this.projectCharts = projectCharts;
    }

    public int getTempNum() {
        return tempNum;
    }

    public void setTempNum(int tempNum) {
        this.tempNum = tempNum;
    }

    public int getDataNum() {
        return dataNum;
    }

    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getTodayTempAdd() {
        return todayTempAdd;
    }

    public void setTodayTempAdd(int todayTempAdd) {
        this.todayTempAdd = todayTempAdd;
    }

    public int getTodayDataAdd() {
        return todayDataAdd;
    }

    public void setTodayDataAdd(int todayDataAdd) {
        this.todayDataAdd = todayDataAdd;
    }
}
