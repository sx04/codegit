package com.cetcbigdata.varanus.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Deprecated
@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "task_warning")
public class TaskWarning {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Integer taskId;

	private Integer listWarningType;

	private String listWarningDetail;

	private Integer docWarningType;

	private String docWarningDetail;

	private String warningTime;
	@Transient
	private String listWarningReason;
	@Transient
	private String docWarningReason;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getListWarningType() {
		return listWarningType;
	}

	public void setListWarningType(Integer listWarningType) {
		this.listWarningType = listWarningType;
	}

	public String getListWarningDetail() {
		return listWarningDetail;
	}

	public void setListWarningDetail(String listWarningDetail) {
		this.listWarningDetail = listWarningDetail;
	}

	public Integer getDocWarningType() {
		return docWarningType;
	}

	public void setDocWarningType(Integer docWarningType) {
		this.docWarningType = docWarningType;
	}

	public String getDocWarningDetail() {
		return docWarningDetail;
	}

	public void setDocWarningDetail(String docWarningDetail) {
		this.docWarningDetail = docWarningDetail;
	}

	public String getWarningTime() {
		return warningTime;
	}

	public void setWarningTime(String warningTime) {
		this.warningTime = warningTime;
	}

	public String getListWarningReason() {
		return listWarningReason;
	}

	public void setListWarningReason(String listWarningReason) {
		this.listWarningReason = listWarningReason;
	}

	public String getDocWarningReason() {
		return docWarningReason;
	}

	public void setDocWarningReason(String docWarningReason) {
		this.docWarningReason = docWarningReason;
	}
}
