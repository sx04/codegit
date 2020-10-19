package com.cetcbigdata.varanus.entity;

import java.io.Serializable;

public class Attachment implements Serializable {

	private String url;

	private String title;

	private String attachment;
	
	private String source; // 来源（网站名称（部门机构名称））

	private String secTitle;
	
	private String insertDate;//插入时间

	private String fileType;//文件类型

	private String myUrl;//上传后的地址

	private Integer attachmentsStatus;//附件下载状态 0代表未下载 1代表已下载 2代表下载失败

	public Integer getAttachmentsStatus() {
		return attachmentsStatus;
	}

	public void setAttachmentsStatus(Integer attachmentsStatus) {
		this.attachmentsStatus = attachmentsStatus;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
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

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMyUrl() {
		return myUrl;
	}

	public void setMyUrl(String myUrl) {
		this.myUrl = myUrl;
	}
}
