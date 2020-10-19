package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "project_info", schema = "data_collection_center", catalog = "")
public class ProjectInfoEntity {
    private String code;//项目主键
    private String name;//项目名称
    private Integer priorLevel;//项目优先级
    private int isCanStart;//配置人员是否可以启动任务的权限
    private String dataTableCode;//项目数据存储表名称
    private Timestamp createTime;//项目创建时间
    private int createUserId;//项目创建人
    private int isRun;//项目状态，1-启动，0-暂停
    private Timestamp runTime;//项目启动暂停时间
    private int isValid;//逻辑删除
    private String dispatchTime;//项目调度时间
    private int isTable;//项目是否为爬取表格的项目,0-不是，1-是

    @Id
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIsTable() {
        return isTable;
    }

    public void setIsTable(int isTable) {
        this.isTable = isTable;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "prior_level")
    public Integer getPriorLevel() {
        return priorLevel;
    }

    public void setPriorLevel(Integer priorLevel) {
        this.priorLevel = priorLevel;
    }

    @Basic
    @Column(name = "is_can_start")
    public int getIsCanStart() {
        return isCanStart;
    }

    public void setIsCanStart(int isCanStart) {
        this.isCanStart = isCanStart;
    }

    @Basic
    @Column(name = "data_table_code")
    public String getDataTableCode() {
        return dataTableCode;
    }

    public void setDataTableCode(String dataTableCode) {
        this.dataTableCode = dataTableCode;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "create_user_id")
    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    @Basic
    @Column(name = "is_run")
    public int getIsRun() {
        return isRun;
    }

    public void setIsRun(int isRun) {
        this.isRun = isRun;
    }

    @Basic
    @Column(name = "run_time")
    public Timestamp getRunTime() {
        return runTime;
    }

    public void setRunTime(Timestamp runTime) {
        this.runTime = runTime;
    }

    @Basic
    @Column(name = "is_valid")
    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    @Basic
    @Column(name = "dispatch_time")
    public String getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInfoEntity that = (ProjectInfoEntity) o;
        return isCanStart == that.isCanStart &&
                createUserId == that.createUserId &&
                isRun == that.isRun &&
                isValid == that.isValid &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(priorLevel, that.priorLevel) &&
                Objects.equals(dataTableCode, that.dataTableCode) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(runTime, that.runTime) &&
                Objects.equals(isTable, that.isTable) &&
                Objects.equals(dispatchTime, that.dispatchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, priorLevel, isCanStart, dataTableCode, createTime, createUserId, isRun, runTime, isValid, dispatchTime,isTable);
    }
}
