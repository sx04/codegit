package com.cetcbigdata.varanus.entity;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-3-14
 * Time:16:26
 */
public class Img {
    private String originPath;
    private String currentPath;
    private Integer imgsStatus;//附件下载状态 0代表未下载 1代表已下载 2代表下载失败

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public Integer getImgsStatus() {
        return imgsStatus;
    }

    public void setImgsStatus(Integer imgsStatus) {
        this.imgsStatus = imgsStatus;
    }
}
