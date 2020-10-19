package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/8/28 13:21
 */
@Entity
@Table(name = "sys_dict", schema = "data_collection_center", catalog = "")
public class SysDictEntity {
    private int id;
    private String typeCode;
    private String text;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type_code")
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Basic
    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysDictEntity that = (SysDictEntity) o;
        return id == that.id &&
                Objects.equals(typeCode, that.typeCode) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeCode, text);
    }
}
