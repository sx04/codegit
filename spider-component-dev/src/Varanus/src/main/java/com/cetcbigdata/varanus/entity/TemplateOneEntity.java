package com.cetcbigdata.varanus.entity;

/**
 * @author sunjunjie
 * @date 2020/8/24 13:58
 */
public class TemplateOneEntity {

    private TaskInfoEntity taskInfoEntity;

    private  TemplateEntity  templateEntity;

    private int commonNumber;

    public int getCommonNumber() {
        return commonNumber;
    }

    public void setCommonNumber(int commonNumber) {
        this.commonNumber = commonNumber;
    }

    public TaskInfoEntity getTaskInfoEntity() {
        return taskInfoEntity;
    }

    public void setTaskInfoEntity(TaskInfoEntity taskInfoEntity) {
        this.taskInfoEntity = taskInfoEntity;
    }

    public TemplateEntity getTemplateEntity() {
        return templateEntity;
    }

    public void setTemplateEntity(TemplateEntity templateEntity) {
        this.templateEntity = templateEntity;
    }
}
