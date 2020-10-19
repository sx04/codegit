package com.cetcbigdata.varanus.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.cetcbigdata.varanus.utils.Util;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Deprecated
@Table(name = "official_document")
@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
//@GenericGenerator(name="system-uuid", strategy = "uuid")
public class OfficialDocument implements Serializable {
	public OfficialDocument() {
		this.keyId = Util.getUUID();
	}

	// private String rowKey; // hbase rowKey
	@JSONField(ordinal=1)
	@Id
	//@GeneratedValue(generator="system-uuid")
	@Column(length = 32)
	private String keyId;// 公文唯一标识符
	@JSONField(ordinal=2)
	private String url; // 网页url
	@JSONField(ordinal=3)
	private String title; // 标题
	@JSONField(ordinal=4)
	private String textTitleInfo;// 信息盒的标题
	@JSONField(ordinal=5)
	private String department; // 网站名称（部门机构名称)
	@JSONField(ordinal=6)
	private String area;// 网站所在行政区域
	@JSONField(ordinal=7)
	private Integer isNormal;// 网站是否正规
	@JSONField(ordinal=8)
	private String policyDate;// 政策日期
	@JSONField(ordinal=9)
	private String sectionTitle; // 章节标题
	@JSONField(ordinal=10)
	private Integer siteWidth;// 网站宽度
	@JSONField(ordinal=11)
	private String attachments; // 附件url，可能是列表
	@JSONField(ordinal=12)
	private String imgs;// 图片路径
	@JSONField(ordinal=13)
	private String topic; // 主题，导航栏主题信息
	@JSONField(ordinal=14)
	private String indexNumberInfo; // 索引号
	@JSONField(ordinal=15)
	private String topicCatInfo; // 信息盒的主题分类
	@JSONField(ordinal=16)
	private String pubOfficeInfo; // 信息盒发文机关
	@JSONField(ordinal=17)
	private String draftDateInfo;// 信息盒成文日期
	@JSONField(ordinal=18)
	private String referenceNumberInfo;// 信息盒的发文字号
	@JSONField(ordinal=19)
	private String pubDateInfo;// 信息盒的发布日期
	@JSONField(ordinal=20)
	private String topicWordsInfo;// 信息盒主题词
	@JSONField(ordinal=21)
	private String summaryInfo;// 信息盒的内容概述
	@JSONField(ordinal=22)
	private String formCodeInfo;// 信息盒的形式代码
	@JSONField(ordinal=23)
	private String themeInfo;// 信息盒的体裁
	@JSONField(ordinal=24)
	private String effectiveDateInfo;// 信息盒的生效日期
	@JSONField(ordinal=25)
	private String expireDateInfo;// 信息盒的失效日期
	@JSONField(ordinal=26)
	private String insertDate;// 文档插入时间
	@JSONField(ordinal=27)
	private String updateDate;// 文档更新时间
	@JSONField(ordinal=28)
	private Integer repeatCount;// 用于判断重复入Redis次数
	@JSONField(ordinal=29)
	private Integer isClean;// 数据是否被清洗，0为未清洗，1为已经清洗
	@JSONField(ordinal=30)
	private String source;// 来源
	@JSONField(ordinal=31)
	private String content; // 正文
	@JSONField(ordinal=32)
	private String textHtml;// 正文源码
	@JSONField(ordinal=33)
	private String sourceHtml;// 网站源代码
	@JSONField(ordinal=34)
	private Integer docId;// 文章唯一标识符
	@JSONField(ordinal=35)
	private Integer listId;// 列表唯一标识符
	@JSONField(ordinal=36)
	private Integer taskId;// 任务唯一标识符
	@JSONField(ordinal = 37)
	private Boolean isFile;//文件地址直接为附件,这种情况不需要再尝试其他模板
	@JSONField(ordinal = 38)
    private String crawlerPolicyOffice;//政策机构
	@JSONField(ordinal = 39)
	private String crawlerBoxPubOfficeInfo;//信息盒内的机构
	@JSONField(ordinal =40)
	private String dataSrcTypeName;//网站数据来源

	public String getDataSrcTypeName() {
		return dataSrcTypeName;
	}

	public void setDataSrcTypeName(String dataSrcTypeName) {
		this.dataSrcTypeName = dataSrcTypeName;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTextHtml() {
		return textHtml;
	}

	public void setTextHtml(String textHtml) {
		this.textHtml = textHtml;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getIndexNumberInfo() {
		return indexNumberInfo;
	}

	public void setIndexNumberInfo(String indexNumberInfo) {
		this.indexNumberInfo = indexNumberInfo;
	}

	public String getTopicCatInfo() {
		return topicCatInfo;
	}

	public void setTopicCatInfo(String topicCatInfo) {
		this.topicCatInfo = topicCatInfo;
	}

	public String getPubOfficeInfo() {
		return pubOfficeInfo;
	}

	public void setPubOfficeInfo(String pubOfficeInfo) {
		this.pubOfficeInfo = pubOfficeInfo;
	}

	public String getDraftDateInfo() {
		return draftDateInfo;
	}

	public void setDraftDateInfo(String draftDateInfo) {
		this.draftDateInfo = draftDateInfo;
	}

	public String getTextTitleInfo() {
		return textTitleInfo;
	}

	public void setTextTitleInfo(String textTitleInfo) {
		this.textTitleInfo = textTitleInfo;
	}

	public String getReferenceNumberInfo() {
		return referenceNumberInfo;
	}

	public void setReferenceNumberInfo(String referenceNumberInfo) {
		this.referenceNumberInfo = referenceNumberInfo;
	}

	public String getPubDateInfo() {
		return pubDateInfo;
	}

	public void setPubDateInfo(String pubDateInfo) {
		this.pubDateInfo = pubDateInfo;
	}

	public String getTopicWordsInfo() {
		return topicWordsInfo;
	}

	public void setTopicWordsInfo(String topicWordsInfo) {
		this.topicWordsInfo = topicWordsInfo;
	}

	public String getSummaryInfo() {
		return summaryInfo;
	}

	public void setSummaryInfo(String summaryInfo) {
		this.summaryInfo = summaryInfo;
	}

	public String getFormCodeInfo() {
		return formCodeInfo;
	}

	public void setFormCodeInfo(String formCodeInfo) {
		this.formCodeInfo = formCodeInfo;
	}

	public String getThemeInfo() {
		return themeInfo;
	}

	public void setThemeInfo(String themeInfo) {
		this.themeInfo = themeInfo;
	}

	public String getEffectiveDateInfo() {
		return effectiveDateInfo;
	}

	public void setEffectiveDateInfo(String effectiveDateInfo) {
		this.effectiveDateInfo = effectiveDateInfo;
	}

	public String getExpireDateInfo() {
		return expireDateInfo;
	}

	public void setExpireDateInfo(String expireDateInfo) {
		this.expireDateInfo = expireDateInfo;
	}

	public String getSourceHtml() {
		return sourceHtml;
	}

	public void setSourceHtml(String sourceHtml) {
		this.sourceHtml = sourceHtml;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getIsNormal() {
		return isNormal;
	}

	public void setIsNormal(Integer isNormal) {
		this.isNormal = isNormal;
	}

	public String getPolicyDate() {
		return policyDate;
	}

	public void setPolicyDate(String policyDate) {
		this.policyDate = policyDate;
	}

	public Integer getSiteWidth() {
		return siteWidth;
	}

	public void setSiteWidth(Integer siteWidth) {
		this.siteWidth = siteWidth;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Integer getIsClean() {
		return isClean;
	}

	public void setIsClean(Integer isClean) {
		this.isClean = isClean;
	}

	public Boolean getIsFile() {
		return isFile;
	}

	public void setIsFile(Boolean isFile) {
		this.isFile = isFile;
	}

	public String getCrawlerPolicyOffice() {
		return crawlerPolicyOffice;
	}

	public void setCrawlerPolicyOffice(String crawlerPolicyOffice) {
		this.crawlerPolicyOffice = crawlerPolicyOffice;
	}

	public String getCrawlerBoxPubOfficeInfo() {
		return crawlerBoxPubOfficeInfo;
	}

	public void setCrawlerBoxPubOfficeInfo(String crawlerBoxPubOfficeInfo) {
		this.crawlerBoxPubOfficeInfo = crawlerBoxPubOfficeInfo;
	}
}
