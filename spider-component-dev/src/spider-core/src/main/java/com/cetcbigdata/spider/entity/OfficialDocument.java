package com.cetcbigdata.spider.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;

public class OfficialDocument implements Serializable {

	private Long id; // 分布式id

	@Indexed(unique = true)
	private String url; // 网页url

	private String source; // 来源（网站名称（部门机构名称））

	private String title; // 标题

	private String text; // 正文

	private String text_html;// 正文源码

	private String sec_title; // 章节标题

	private List<String> attachment; // 附件url，可能是列表

	private String topic; // 主题，导航栏主题信息

	private String index_number_info; // 索引号

	private String index_number_info_alias; // 索引号在该网站的原名

	private String topic_cat_info; // 信息盒的主题分类

	private String topic_cat_info_alias;// 信息盒的主题分类原名

	private String pub_office_info; // 信息盒发文机关

	private String pub_office_info_alias; // 信息盒发文机关原名

	private String draft_date_info;// 信息盒成文日期

	private String draft_date_info_alias; // 信息盒成文日期原名

	private String text_title_info;// 信息盒的标题

	private String text_title_info_alias; // 信息盒的标题原文

	private String reference_number_info;// 信息盒的发文字号

	private String reference_number_info_alias;// 信息盒的发文字号原文

	private String pub_date_info;// 信息盒的发布日期

	private String pub_date_info_alias;// 信息盒的发布日期原文

	private String topic_words_info;// 信息盒主题词

	private String topic_words_info_alias;// 信息盒主题词原文

	private String source_info; // 信息盒的信息来源

	private String source_info_alias;// 信息盒信息来源原名

	private String summary_info;// 信息盒的内容概述

	private String summary_info_alias;// 信息盒的内容概述原名

	private String form_code_info;// 信息盒的形式代码

	private String form_code_info_alias;// 信息盒的形式代码原名

	private String theme_info;// 信息盒的体裁

	private String theme_info_alias;// 信息盒的体裁原名

	private String effective_date_info;// 信息盒的生效日期

	private String effective_date_info_alias;// 信息盒的生效日期原名

	private String expire_date_info;// 信息盒的失效日期

	private String expire_date_info_alias;// 信息盒的失效日期原名

	private String source_html;//网站源代码

	private String insert_date;//文档插入时间

	private String update_date;//文档更新时间

	private String area;//网站所在行政区域

	private String isNormal;//网站是否正规

	private String policy_date;//政策日期

	private List<Attachment> attachments; // 附件url，可能是列表

	private List<Img> imgs;//图片路径

	private String site_width;//网站宽度

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText_html() {
		return text_html;
	}

	public void setText_html(String text_html) {
		this.text_html = text_html;
	}

	public List<String> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<String> attachment) {
		this.attachment = attachment;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getIndex_number_info() {
		return index_number_info;
	}

	public void setIndex_number_info(String index_number_info) {
		this.index_number_info = index_number_info;
	}

	public String getIndex_number_info_alias() {
		return index_number_info_alias;
	}

	public void setIndex_number_info_alias(String index_number_info_alias) {
		this.index_number_info_alias = index_number_info_alias;
	}

	public String getTopic_cat_info() {
		return topic_cat_info;
	}

	public void setTopic_cat_info(String topic_cat_info) {
		this.topic_cat_info = topic_cat_info;
	}

	public String getTopic_cat_info_alias() {
		return topic_cat_info_alias;
	}

	public void setTopic_cat_info_alias(String topic_cat_info_alias) {
		this.topic_cat_info_alias = topic_cat_info_alias;
	}

	public String getPub_office_info() {
		return pub_office_info;
	}

	public void setPub_office_info(String pub_office_info) {
		this.pub_office_info = pub_office_info;
	}

	public String getPub_office_info_alias() {
		return pub_office_info_alias;
	}

	public void setPub_office_info_alias(String pub_office_info_alias) {
		this.pub_office_info_alias = pub_office_info_alias;
	}

	public String getDraft_date_info() {
		return draft_date_info;
	}

	public void setDraft_date_info(String draft_date_info) {
		this.draft_date_info = draft_date_info;
	}

	public String getDraft_date_info_alias() {
		return draft_date_info_alias;
	}

	public void setDraft_date_info_alias(String draft_date_info_alias) {
		this.draft_date_info_alias = draft_date_info_alias;
	}

	public String getText_title_info() {
		return text_title_info;
	}

	public void setText_title_info(String text_title_info) {
		this.text_title_info = text_title_info;
	}

	public String getText_title_info_alias() {
		return text_title_info_alias;
	}

	public void setText_title_info_alias(String text_title_info_alias) {
		this.text_title_info_alias = text_title_info_alias;
	}

	public String getReference_number_info() {
		return reference_number_info;
	}

	public void setReference_number_info(String reference_number_info) {
		this.reference_number_info = reference_number_info;
	}

	public String getReference_number_info_alias() {
		return reference_number_info_alias;
	}

	public void setReference_number_info_alias(String reference_number_info_alias) {
		this.reference_number_info_alias = reference_number_info_alias;
	}

	public String getPub_date_info() {
		return pub_date_info;
	}

	public void setPub_date_info(String pub_date_info) {
		this.pub_date_info = pub_date_info;
	}

	public String getPub_date_info_alias() {
		return pub_date_info_alias;
	}

	public void setPub_date_info_alias(String pub_date_info_alias) {
		this.pub_date_info_alias = pub_date_info_alias;
	}

	public String getTopic_words_info() {
		return topic_words_info;
	}

	public void setTopic_words_info(String topic_words_info) {
		this.topic_words_info = topic_words_info;
	}

	public String getTopic_words_info_alias() {
		return topic_words_info_alias;
	}

	public void setTopic_words_info_alias(String topic_words_info_alias) {
		this.topic_words_info_alias = topic_words_info_alias;
	}

	public String getSource_info() {
		return source_info;
	}

	public void setSource_info(String source_info) {
		this.source_info = source_info;
	}

	public String getSource_info_alias() {
		return source_info_alias;
	}

	public void setSource_info_alias(String source_info_alias) {
		this.source_info_alias = source_info_alias;
	}

	public String getSummary_info() {
		return summary_info;
	}

	public void setSummary_info(String summary_info) {
		this.summary_info = summary_info;
	}

	public String getSummary_info_alias() {
		return summary_info_alias;
	}

	public void setSummary_info_alias(String summary_info_alias) {
		this.summary_info_alias = summary_info_alias;
	}

	public String getForm_code_info() {
		return form_code_info;
	}

	public void setForm_code_info(String form_code_info) {
		this.form_code_info = form_code_info;
	}

	public String getForm_code_info_alias() {
		return form_code_info_alias;
	}

	public void setForm_code_info_alias(String form_code_info_alias) {
		this.form_code_info_alias = form_code_info_alias;
	}

	public String getTheme_info() {
		return theme_info;
	}

	public void setTheme_info(String theme_info) {
		this.theme_info = theme_info;
	}

	public String getTheme_info_alias() {
		return theme_info_alias;
	}

	public void setTheme_info_alias(String theme_info_alias) {
		this.theme_info_alias = theme_info_alias;
	}

	public String getEffective_date_info() {
		return effective_date_info;
	}

	public void setEffective_date_info(String effective_date_info) {
		this.effective_date_info = effective_date_info;
	}

	public String getEffective_date_info_alias() {
		return effective_date_info_alias;
	}

	public void setEffective_date_info_alias(String effective_date_info_alias) {
		this.effective_date_info_alias = effective_date_info_alias;
	}

	public String getExpire_date_info() {
		return expire_date_info;
	}

	public void setExpire_date_info(String expire_date_info) {
		this.expire_date_info = expire_date_info;
	}

	public String getExpire_date_info_alias() {
		return expire_date_info_alias;
	}

	public void setExpire_date_info_alias(String expire_date_info_alias) {
		this.expire_date_info_alias = expire_date_info_alias;
	}

	public String getSec_title() {
		return sec_title;
	}

	public void setSec_title(String sec_title) {
		this.sec_title = sec_title;
	}

	public String getSource_html() {
		return source_html;
	}

	public void setSource_html(String source_html) {
		this.source_html = source_html;
	}

	public String getInsert_date() {
		return insert_date;
	}

	public void setInsert_date(String insert_date) {
		this.insert_date = insert_date;
	}

	public String getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getIsNormal() {
		return isNormal;
	}

	public void setIsNormal(String isNormal) {
		this.isNormal = isNormal;
	}

	public String getPolicy_date() {
		return policy_date;
	}

	public void setPolicy_date(String policy_date) {
		this.policy_date = policy_date;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Img> getImgs() {
		return imgs;
	}

	public void setImgs(List<Img> imgs) {
		this.imgs = imgs;
	}

	public String getSite_width() {
		return site_width;
	}

	public void setSite_width(String site_width) {
		this.site_width = site_width;
	}
}
