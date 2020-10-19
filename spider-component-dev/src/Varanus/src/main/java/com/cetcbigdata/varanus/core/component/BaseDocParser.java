package com.cetcbigdata.varanus.core.component;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.entity.Attachment;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:16:00
 * 
 * @author matthew
 *
 *         页面解析器基类
 */
@Component
public abstract class BaseDocParser {

	private static final Logger LOG = LoggerFactory.getLogger(BaseDocParser.class);
	@Autowired
	public WebClientPool webClientPool;
	@Value("${varanus.file.attachmentsPath}")
	public String attachmentsPath;
	@Value("${varanus.file.imgsPath}")
	public String imgsPath;
	@Value("${varanus.file.localAttachmentsUrl}")
	public String localAttachmentsUrl;
	@Value("${varanus.file.localImgsUrl}")
	public String localImgsUrl;

	private OkHttpClient okHttpClient = new OkHttpClient();
	// 文档详情处理信息
	protected abstract OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument,
			ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy)
			throws Exception;
	// 文档详情处理信息
	public  OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument,
												   ThreadLocal<WebDriver> webClient, OkHttpClient okHttpClient, Boolean needProxy)
			throws Exception{
		return null;
	}

	// 构建基本信息
	protected OfficialDocument buildBaseInfo(OfficialDocument officialDocument) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(new Date());
		officialDocument.setInsertDate(date);
		officialDocument.setIsFile(false);
		/*
		 * officialDocument.setSec_title(sec_title); officialDocument.setSource(source);
		 * officialDocument.setInsert_date(date);
		 * 
		 * Long id = idWorker.nextId(); officialDocument.setId(id);
		 * 
		 * String url = officialDocumentList.getHref(); officialDocument.setUrl(url);
		 * officialDocument.setTitle(officialDocumentList.getName());
		 * officialDocument.setPub_date_info(officialDocumentList.getTime());
		 * officialDocument.setArea(area);
		 */
		return officialDocument;
	}


	public void saveAttachment(OfficialDocument officialDocument,String attachmentsPath){
		Attachment atta = new Attachment();
		atta.setAttachmentsStatus(0);
		String filePath = PageParserHelper.download(officialDocument.getUrl(),atta, attachmentsPath,okHttpClient);
		atta.setUrl(officialDocument.getUrl());
		atta.setTitle(officialDocument.getTitle());
		atta.setMyUrl(StringUtils.isBlank(filePath)?"":filePath);
		officialDocument.setAttachments(JSON.toJSONString(atta));
		officialDocument.setIsFile(true);
	}

	// 解析页面
	protected abstract void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage,
			Response response) throws Exception;


}
