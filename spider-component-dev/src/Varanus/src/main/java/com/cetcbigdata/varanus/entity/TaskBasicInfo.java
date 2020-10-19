package com.cetcbigdata.varanus.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 
 * 文章基本信息表实例
 * 
 * @author 宋旻雨
 * 
 * @Time 2019-03-26
 *
 * 
 * 
 */
@Deprecated
@Entity
@Table(name = "task_basic_info") // 表名
public class TaskBasicInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer taskId;// 任务唯一标识符

	private String sqlTable;// MySQL存储表名

	private String department;// 文章发布部门

	private String area;// 文章发布部门所属行政区域

	private String sectionTitle;// 任务板块标题

	private String sectionUrl;// 板块url

	private Integer isValid;// 配置是否有效，0-无效 1-有效

	private Date insertTime;// 文章插入时间

	private Date updateDate;// 文章更新时间

	private Boolean urlAvailability;// 板块列表首页url网络是否可达，0-无效 1-有效

	private String siteVersion; // 网站版本

	private String versionExceptionCount; // 网站版本异常次数

	private String responsiblePeople; // 任务负责人

	private String domainName;//领域

	private String dataSrcTypeName;//数据源类型

	public String getDataSrcTypeName() {
		return dataSrcTypeName;
	}

	public void setDataSrcTypeName(String dataSrcTypeName) {
		this.dataSrcTypeName = dataSrcTypeName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getSqlTable() {
		return sqlTable;
	}

	public void setSqlTable(String sqlTable) {
		this.sqlTable = sqlTable;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public String getSectionUrl() {
		return sectionUrl;
	}

	public void setSectionUrl(String sectionUrl) {
		this.sectionUrl = sectionUrl;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Boolean getUrlAvailability() {
		return urlAvailability;
	}

	public void setUrlAvailability(Boolean urlAvailability) {
		this.urlAvailability = urlAvailability;
	}

	public String getSiteVersion() {
		return siteVersion;
	}

	public void setSiteVersion(String siteVersion) {
		this.siteVersion = siteVersion;
	}

	public String getVersionExceptionCount() {
		return versionExceptionCount;
	}

	public void setVersionExceptionCount(String versionExceptionCount) {
		this.versionExceptionCount = versionExceptionCount;
	}

	public String getResponsiblePeople() {
		return responsiblePeople;
	}

	public void setResponsiblePeople(String responsiblePeople) {
		this.responsiblePeople = responsiblePeople;
	}
}
