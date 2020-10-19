package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * @author sunjunjie
 * @date 2020/8/14 14:14
 */

public class ProjectBasicEntity {

    private String code;
    private String name;
    private String  dispatchTime;
    private String  priorLevel;
    private String users;
    private int isRun;

    public int getIsRun() {
        return isRun;
    }

    public void setIsRun(int isRun) {
        this.isRun = isRun;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getPriorLevel() {
        return priorLevel;
    }

    public void setPriorLevel(String priorLevel) {
        this.priorLevel = priorLevel;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
