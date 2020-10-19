package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "project_user", schema = "data_collection_center", catalog = "")
public class ProjectUserEntity {
    private int id;
    private int userId;
    private String projectId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "project_id")
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectUserEntity that = (ProjectUserEntity) o;
        return id == that.id &&
                userId == that.userId &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, projectId);
    }
}
