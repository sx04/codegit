package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
@Entity
public class UserEntity {


    @Id
    private int id;
    private String userName;
    private String isAdmin;

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
