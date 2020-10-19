package com.cetcbigdata.spider.entity;

import java.io.Serializable;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2018-11-8
 * Time:13:53
 */
public class Message implements Serializable {
    private String dataBase;
    private String collection;
    private Long timestamp;
    private String area;

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
