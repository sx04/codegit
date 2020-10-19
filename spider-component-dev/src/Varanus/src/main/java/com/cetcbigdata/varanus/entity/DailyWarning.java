package com.cetcbigdata.varanus.entity;

import java.math.BigInteger;

public class DailyWarning {

	private Integer taskId;
	private BigInteger listNetworkCount;
	private BigInteger listUrlCount;
	private BigInteger listTemplateCount;
	private BigInteger docNetworkCount;
	private BigInteger docUrlCount;
	private BigInteger docTemplateCount;

	public DailyWarning(Integer taskId, BigInteger listNetworkCount, BigInteger listUrlCount,
			BigInteger listTemplateCount, BigInteger docNetworkCount, BigInteger docUrlCount,
			BigInteger docTemplateCount) {
		this.taskId = taskId;
		this.listNetworkCount = listNetworkCount;
		this.listUrlCount = listUrlCount;
		this.listTemplateCount = listTemplateCount;
		this.docNetworkCount = docNetworkCount;
		this.docUrlCount = docUrlCount;
		this.docTemplateCount = docTemplateCount;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public BigInteger getListNetworkCount() {
		return listNetworkCount;
	}

	public void setListNetworkCount(BigInteger listNetworkCount) {
		this.listNetworkCount = listNetworkCount;
	}

	public BigInteger getListUrlCount() {
		return listUrlCount;
	}

	public void setListUrlCount(BigInteger listUrlCount) {
		this.listUrlCount = listUrlCount;
	}

	public BigInteger getListTemplateCount() {
		return listTemplateCount;
	}

	public void setListTemplateCount(BigInteger listTemplateCount) {
		this.listTemplateCount = listTemplateCount;
	}

	public BigInteger getDocNetworkCount() {
		return docNetworkCount;
	}

	public void setDocNetworkCount(BigInteger docNetworkCount) {
		this.docNetworkCount = docNetworkCount;
	}

	public BigInteger getDocUrlCount() {
		return docUrlCount;
	}

	public void setDocUrlCount(BigInteger docUrlCount) {
		this.docUrlCount = docUrlCount;
	}

	public BigInteger getDocTemplateCount() {
		return docTemplateCount;
	}

	public void setDocTemplateCount(BigInteger docTemplateCount) {
		this.docTemplateCount = docTemplateCount;
	}
}
