package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/8/14 14:04
 */
@Entity
@Table(name = "project_file", schema = "data_collection_center", catalog = "")
public class ProjectFileEntity {
    private int id;
    private String projectId;
    private String fileCode;
    private String fileName;
    private int fileSort;
    private String fileType;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "project_id")
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Basic
    @Column(name = "file_code")
    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    @Basic
    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "file_sort")
    public int getFileSort() {
        return fileSort;
    }

    public void setFileSort(int fileSort) {
        this.fileSort = fileSort;
    }

    @Basic
    @Column(name = "file_type")
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectFileEntity that = (ProjectFileEntity) o;
        return id == that.id &&
                fileSort == that.fileSort &&
                Objects.equals(projectId, that.projectId) &&
                Objects.equals(fileCode, that.fileCode) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, fileCode, fileName, fileSort, fileType);
    }
}
