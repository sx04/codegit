package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "project_interface", schema = "data_collection_center", catalog = "")
public class ProjectInterfaceEntity {
    private int id;
    private String interfaceCode;
    private String interfaceClassName;
    private String filePath;
    private int uploadUserId;
    private Timestamp uploadTime;
    private Integer isSuccess;
    private String errorMessage;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "interface_code")
    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    @Basic
    @Column(name = "interface_class_name")
    public String getInterfaceClassName() {
        return interfaceClassName;
    }

    public void setInterfaceClassName(String interfaceClassName) {
        this.interfaceClassName = interfaceClassName;
    }

    @Basic
    @Column(name = "file_path")
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Basic
    @Column(name = "upload_user_id")
    public int getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(int uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    @Basic
    @Column(name = "upload_time")
    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Basic
    @Column(name = "is_success")
    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Basic
    @Column(name = "error_message")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInterfaceEntity that = (ProjectInterfaceEntity) o;
        return id == that.id &&
                uploadUserId == that.uploadUserId &&
                Objects.equals(interfaceCode, that.interfaceCode) &&
                Objects.equals(interfaceClassName, that.interfaceClassName) &&
                Objects.equals(filePath, that.filePath) &&
                Objects.equals(uploadTime, that.uploadTime) &&
                Objects.equals(isSuccess, that.isSuccess) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, interfaceCode, interfaceClassName, filePath, uploadUserId, uploadTime, isSuccess, errorMessage);
    }
}
