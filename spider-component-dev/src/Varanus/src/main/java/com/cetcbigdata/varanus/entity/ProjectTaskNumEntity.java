package com.cetcbigdata.varanus.entity;

/**
 * @author sunjunjie
 * @date 2020/9/2 15:41
 */

public class ProjectTaskNumEntity {

    private String projectName;
    private int taskTotal;
    private int taskGet;
    private int taskFinish;
    private int taskUnfinish;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(int taskTotal) {
        this.taskTotal = taskTotal;
    }

    public int getTaskGet() {
        return taskGet;
    }

    public void setTaskGet(int taskGet) {
        this.taskGet = taskGet;
    }

    public int getTaskFinish() {
        return taskFinish;
    }

    public void setTaskFinish(int taskFinish) {
        this.taskFinish = taskFinish;
    }

    public int getTaskUnfinish() {
        return taskUnfinish;
    }

    public void setTaskUnfinish(int taskUnfinish) {
        this.taskUnfinish = taskUnfinish;
    }

    public ProjectTaskNumEntity(int taskTotal, int taskGet, int taskFinish, int taskUnfinish) {
        this.taskTotal = taskTotal;
        this.taskGet = taskGet;
        this.taskFinish = taskFinish;
        this.taskUnfinish = taskUnfinish;
    }
}
