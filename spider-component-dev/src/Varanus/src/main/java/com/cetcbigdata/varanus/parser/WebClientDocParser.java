package com.cetcbigdata.varanus.parser;

import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.BaseDocParser;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.entity.TaskWarning;
import com.cetcbigdata.varanus.utils.Constant;
import com.cetcbigdata.varanus.utils.HumpToLine;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.beans.binding.IntegerBinding;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.soap.Text;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:15:00
 * 
 * @author matthew
 */
@Deprecated
@Component
public class WebClientDocParser extends BaseDocParser {

	@Autowired
	private TaskWarningDAO taskWarningDAO;

	private OkHttpClient okHttpClient = new OkHttpClient();

	private static final Logger LOG = LoggerFactory.getLogger(WebClientDocParser.class);

	@Override
	public OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument,
									   ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy)
			throws Exception {
		buildBaseInfo(officialDocument);
		if (Constants.DOC_RESPONSETYPE_HTML.equals(docDetail.getDocResponseType())) {
			Object o = PageParserHelper.getHtmlPage(webClient, webRequest);
			if (o == null) {
			/*	TaskWarning taskWarning = new TaskWarning();
				taskWarning.setTaskId(docDetail.getTaskId());
				taskWarning.setDocWarningType(Constants.URL_WARNING);
				taskWarning.setDocWarningDetail("url:" + officialDocument.getUrl() + "访问失败");
				taskWarningDAO.save(taskWarning);*/
				LOG.info("网址访问失败");
			} else if (o != null && !(o instanceof HtmlPage)) {
				//地址为附件，直接进行附件下载
				saveAttachment(officialDocument, attachmentsPath);
			} else {
				HtmlPage htmlPage = (HtmlPage) o;
				parsePage(docDetail, officialDocument, htmlPage, null);
			}
		}
		/*
		 * if ("JSON".equals(docDetail.getDocResponseType())) { //通过json处理请求 Request
		 * request = new Request.Builder().url(officialDocument.getUrl()).build();
		 * Response response = null; response = okHttpClient.newCall(request).execute();
		 * parsePage(docDetail, officialDocument, null,response); }
		 */
		return officialDocument;
	}


	@Override
	public void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage, Response response)
			throws Exception {
		/*// 处理HTML返回
		if (Optional.ofNullable(htmlPage).isPresent()) {
			htmlPageParser(docDetail, officialDocument, htmlPage);
		}*/
	}

	//dynamicMap存动态Xpath  detailMap存固有值  detailData存爬取的数据
	public HashMap htmlPageParser(HashMap<String, String> dynamicMap, HtmlPage htmlPage, HashMap detailMap, HashMap detailData, String webName) {
		okHttpClient.dispatcher().setMaxRequests(128);
		okHttpClient.dispatcher().setMaxRequestsPerHost(16);
		String siteWidth = detailMap.get("siteWidth").toString();
		String sourceHtml = detailMap.get("sourceHtml").toString();
		//存储网站宽度
		if (!StringUtils.isEmpty(siteWidth)) {
			detailData.put("siteWidth", siteWidth);
		}
		//存储网站源码
		if (!StringUtils.isEmpty(sourceHtml)) {
			DomElement domNode = htmlPage.getFirstByXPath(sourceHtml);

			String textHtml = domNode.asXml();
			detailData.put("sourceHtml", textHtml);
		}

		//循环遍历dynamicMap的Xpath的值并根据xpath爬取数据存储在map中返回
		for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
			String xpathKey = entry.getKey();
			String xpathValue = entry.getValue();
			if (!StringUtils.isEmpty(xpathValue)) {
				Object domNode = htmlPage.getFirstByXPath(xpathValue);
				if (domNode instanceof DomElement) {
					if (domNode != null) {
						DomElement d = (DomElement)domNode;
						String href =((DomElement)domNode).getAttribute("href");
						//判断是否为附件
						if (!StringUtils.isEmpty(href)) {
							// 处理附件
							PageParserHelper.addAttachmentsHtml(htmlPage, xpathValue, xpathKey,
									Util.fileName(webName, attachmentsPath), detailData, okHttpClient);
							//判断是否为图片
						} else if ("img".equals(d.getTagName())) {
							// 处理图片
							PageParserHelper.addImgs(detailData, htmlPage, xpathValue, xpathKey,
									Util.fileName(webName, imgsPath), localImgsUrl, okHttpClient);
						} else {
							// 处理其他自定义Xpath
							String data = d.asText();
							if (!StringUtils.isBlank(data)) {
								detailData.put(xpathKey, data);
							}
						}
					}
				} else if(domNode instanceof Text){
					detailData.put(xpathKey, domNode);
				}
			}
		}
		return detailData;
	}
}
