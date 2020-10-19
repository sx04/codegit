package com.cetcbigdata.varanus.entity;


import java.util.List;
/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
public class ProjectInfoUserEntity {

    private ProjectInfoEntity projectInfoEntity;

    private List<UserEntity> sysUsers;

    private List<ProjectFileEntity> projectFileEntities;

    public List<ProjectFileEntity> getProjectFileEntities() {
        return projectFileEntities;
    }

    public void setProjectFileEntities(List<ProjectFileEntity> projectFileEntities) {
        this.projectFileEntities = projectFileEntities;
    }

    public ProjectInfoEntity getProjectInfoEntity() {
        return projectInfoEntity;
    }

    public void setProjectInfoEntity(ProjectInfoEntity projectInfoEntity) {
        this.projectInfoEntity = projectInfoEntity;
    }

    public List<UserEntity> getSysUsers() {
        return sysUsers;
    }

    public void setSysUsers(List<UserEntity> sysUsers) {
        this.sysUsers = sysUsers;
    }
}
