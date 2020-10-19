package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "audit_log", schema = "data_collection_center", catalog = "")
public class AuditLogEntity {
    private int id;
    private int userId;
    private String moduleCode;
    private String operType;
    private Timestamp operTime;
    private String content;

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
    @Column(name = "module_code")
    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    @Basic
    @Column(name = "oper_type")
    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    @Basic
    @Column(name = "oper_time")
    public Timestamp getOperTime() { return operTime;
    }

    public void setOperTime(Timestamp operTime) {
        this.operTime = operTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLogEntity that = (AuditLogEntity) o;
        return id == that.id &&
                userId == that.userId &&
                Objects.equals(moduleCode, that.moduleCode) &&
                Objects.equals(operType, that.operType) &&
                Objects.equals(operTime, that.operTime) &&
                Objects.equals(content, that.content) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, moduleCode, operType, operTime, content);
    }
}
