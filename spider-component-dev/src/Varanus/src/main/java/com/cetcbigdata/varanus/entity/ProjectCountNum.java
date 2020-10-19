package com.cetcbigdata.varanus.entity;


/**
 * @author sunjunjie
 * @date 2020/9/3 9:55
 */

public class ProjectCountNum {

    private String code;
    private String name;
    private int dataNum;
    private int tempNum;
    private int warningNum;
    private int unGet;

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

    public int getDataNum() {
        return dataNum;
    }

    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public int getTempNum() {
        return tempNum;
    }

    public void setTempNum(int tempNum) {
        this.tempNum = tempNum;
    }

    public int getWarningNum() {
        return warningNum;
    }

    public void setWarningNum(int warningNum) {
        this.warningNum = warningNum;
    }

    public int getUnGet() {
        return unGet;
    }

    public void setUnGet(int unGet) {
        this.unGet = unGet;
    }
}
