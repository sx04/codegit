package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.util.Objects;
@Deprecated
@Entity
@Table(name = "detail_zhiwen_data", schema = "data_collection_center", catalog = "")
public class DetailZhiwenDataEntity {
    private String id;
    private Integer listId;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "list_id")
    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailZhiwenDataEntity that = (DetailZhiwenDataEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(listId, that.listId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, listId);
    }
}
