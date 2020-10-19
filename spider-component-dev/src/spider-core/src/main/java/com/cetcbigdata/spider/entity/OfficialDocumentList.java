package com.cetcbigdata.spider.entity;

import java.io.Serializable;

public class OfficialDocumentList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5177777380118695506L;

	private String name;

	private String href;

	private String index;

	private String time;

	private String taskClass;

	private int crawlerCount;

	private Boolean agagin;// 重复抓取 不加入bloom过滤	

	private String source; // 来源（网站名称（部门机构名称））

	private String secTitle;
	
	private String insertDate;//插入时间
	

	public int getCrawlerCount() {
		return crawlerCount;
	}

	public void setCrawlerCount(int crawlerCount) {
		this.crawlerCount = crawlerCount;
	}

	public String getTaskClass() {
		return taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getAgagin() {
		return agagin;
	}

	public void setAgagin(Boolean agagin) {
		this.agagin = agagin;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSecTitle() {
		return secTitle;
	}

	public void setSecTitle(String secTitle) {
		this.secTitle = secTitle;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	
	
}
