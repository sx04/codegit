package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:15
 */
@Entity
@Table(name = "table_column_info", schema = "data_collection_center", catalog = "")
public class TableColumnInfoEntity {
    private int id;
    private String columnCode;
    private String columnName;
    private int columnNum;
    private int taskId;
    private String type;
    private String value;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "column_code")
    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    @Basic
    @Column(name = "column_name")
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Basic
    @Column(name = "column_num")
    public int getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

    @Basic
    @Column(name = "task_id")
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumnInfoEntity that = (TableColumnInfoEntity) o;
        return id == that.id &&
                columnNum == that.columnNum &&
                taskId == that.taskId &&
                Objects.equals(columnCode, that.columnCode) &&
                Objects.equals(columnName, that.columnName) &&
                Objects.equals(type, that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, columnCode, columnName, columnNum, taskId, type, value);
    }
}
