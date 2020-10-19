package com.cetcbigdata.varanus.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author sunjunjie
 * @date 2020/8/14 10:29
 */
@Entity
@Table(name = "template", schema = "data_collection_center", catalog = "")
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;//模板id
    @Basic
    @Column(name = "task_id")
    private int taskId;//任务id
    @Basic
    @Column(name = "list_template_url")
    private String listTemplateUrl;//第二页及以后列表规则模板url
    @Basic
    @Column(name = "list_page_number")
    private int listPageNumber;//文章列表总页数
    @Basic
    @Column(name = "doc_number")
    private Integer docNumber;//每页文章标题数
    @Basic
    @Column(name = "group_id")
    private Integer groupId;//任务共用模板id,共用则填写共用模板的listId否则为自己的listid
    @Basic
    @Column(name = "is_correct")
    private int isCorrect;//任务下模板是否正确，0-错误，1-正确 ，2-未验证
    @Basic
    @Column(name = "is_run")
    private int isRun;//采集任务启动，0-暂停，1-启动
    @Basic
    @Column(name = "run_time")
    private Timestamp runTime;//任务启动或暂停时间


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


    public String getListTemplateUrl() {
        return listTemplateUrl;
    }

    public void setListTemplateUrl(String listTemplateUrl) {
        this.listTemplateUrl = listTemplateUrl;
    }


    public int getListPageNumber() {
        return listPageNumber;
    }

    public void setListPageNumber(int listPageNumber) {
        this.listPageNumber = listPageNumber;
    }


    public Integer getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(Integer docNumber) {
        this.docNumber = docNumber;
    }


    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }


    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }


    public int getIsRun() {
        return isRun;
    }

    public void setIsRun(int isRun) {
        this.isRun = isRun;
    }


    public Timestamp getRunTime() {
        return runTime;
    }

    public void setRunTime(Timestamp runTime) {
        this.runTime = runTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateEntity that = (TemplateEntity) o;
        return id == that.id &&
                taskId == that.taskId &&
                listPageNumber == that.listPageNumber &&
                isCorrect == that.isCorrect &&
                isRun == that.isRun &&
                Objects.equals(listTemplateUrl, that.listTemplateUrl) &&
                Objects.equals(docNumber, that.docNumber) &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(runTime, that.runTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, listTemplateUrl, listPageNumber, docNumber, groupId, isCorrect, isRun, runTime);
    }
}
