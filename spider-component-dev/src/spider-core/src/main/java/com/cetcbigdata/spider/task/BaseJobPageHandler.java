package com.cetcbigdata.spider.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cetcbigdata.spider.entity.Attachment;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.cetcbigdata.spider.entity.OfficialDocument;
import com.cetcbigdata.spider.entity.OfficialDocumentList;
import com.cetcbigdata.spider.util.CharsetDetector;
import com.cetcbigdata.spider.util.Util;
import com.cetcbigdata.spider.work.ITask;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.xxl.job.core.handler.IJobHandler;

import okhttp3.OkHttpClient;

/**
 * @author yangkunlin
 * @version 创建时间：2018年9月25日 上午10:55:30 类说明
 */
public abstract class BaseJobPageHandler extends IJobHandler {

	private static final Logger LOG = LoggerFactory.getLogger(BaseJobPageHandler.class);

	abstract void createIndex(String collectionName);

	abstract void saveData(OfficialDocument officialDocument, String collectionName);

	abstract void returnQueue(String taskQueue, OfficialDocumentList officialDocumentList);

	abstract void exceptionWebHandler(String taskQueue, OfficialDocumentList officialDocumentList, ITask iTask,
			Boolean proxy);

	abstract OkHttpClient exceptionHttpHandler(String taskQueue, OfficialDocumentList officialDocumentList,
			ITask iTask);

	abstract void exceptionWebDriverHandler(String taskQueue, OfficialDocumentList officialDocumentList, ITask iTask);

	public List<DomNode> getDomNodeList(HtmlPage htmlPage, String herfpath) {
		if (htmlPage == null || StringUtils.isEmpty(herfpath)) {
			return null;
		}

		List<DomNode> domNodes = htmlPage.getByXPath(herfpath);
		return domNodes;
	}

	/**
	 * * 查找标签中HtmlAnchor子标签
	 * 
	 * @param e
	 * @param htmlPage
	 * @param herfpath
	 * @return
	 */
	public HtmlAnchor getLinkDomNode(DomNode e, HtmlPage htmlPage) {
		if (e == null || htmlPage == null) {
			return null;
		}
		HtmlAnchor link = null;
		List<DomNode> list = e.getChildNodes();

		for (DomNode node : list) {
			if (node instanceof HtmlAnchor) {
				link = (HtmlAnchor) node;
			}
		}
		if (link == null) {
			LOG.error(" {} in this url {} hasn't a HtmlAnchor", htmlPage.getUrl(), e.getNodeName());
			return null;
		}
		return (HtmlAnchor) link;
	}

	/**
	 * * 查找标签中指定的 timeElementName子标签
	 * 
	 * @param e
	 * @param htmlPage
	 * @param timeElementName
	 * @return
	 */
	public DomNode getTimeDomNode(DomNode e, HtmlPage htmlPage, String timeElementName) {
		if (e == null || htmlPage == null || StringUtils.isEmpty(timeElementName)) {
			return null;
		}
		List<DomNode> list = e.getChildNodes();
		DomNode time = null;
		for (DomNode node : list) {
			if (node.getNodeName().equals(timeElementName)) {
				time = node;
			}
		}
		if (time == null) {
			LOG.error(" {} in this url {} hasn't a {}", htmlPage.getUrl(), e.getNodeName(), timeElementName);
			return null;
		}
		return time;
	}

	public OfficialDocumentList getOfficialDocumentList(Element e, String baeUrl, boolean sameHost, String className) {
		String href = getAbsUrl(e, baeUrl, sameHost);
		if (!StringUtils.isEmpty(href)) {
			OfficialDocumentList officialDocumentList = new OfficialDocumentList();
			officialDocumentList.setHref(href);
			officialDocumentList.setTaskClass(className);
			officialDocumentList.setCrawlerCount(0);
			String title = e.attr("title");
			if (StringUtils.isBlank(title)) {
				title = e.text();
			}
			officialDocumentList.setName(title);
			return officialDocumentList;
		}
		return null;
	}

	public OfficialDocumentList getOfficialDocumentList(DomNode dom, HtmlPage htmlPage, boolean sameHost,
			String className) {
		String herf = getUrl((HtmlAnchor) dom, htmlPage, sameHost);
		if (!StringUtils.isEmpty(herf)) {
			OfficialDocumentList officialDocumentList = getOfficialDocumentList(herf, className, dom);
			return officialDocumentList;
		}
		return null;
	}

	public OfficialDocumentList getOfficialDocumentList(String href, String className, DomNode link) {
		if (StringUtils.isEmpty(href) || StringUtils.isEmpty(className)) {
			return null;
		}
		OfficialDocumentList officialDocumentList = new OfficialDocumentList();
		officialDocumentList.setHref(href);
		officialDocumentList.setTaskClass(className);
		officialDocumentList.setCrawlerCount(0);
		String title = link.getTextContent();
		NamedNodeMap attrs = link.getAttributes();
		if (attrs != null) {
			Node node = attrs.getNamedItem("title");
			if (node != null && !StringUtils.isBlank(node.getTextContent())) {
				title = node.getTextContent();
			}
		}

		officialDocumentList.setName(title);

		return officialDocumentList;
	}

	/***
	 * 如果获取的时间和url不能通过getLinkDomNode(dom, htmlPage)和getTimeDomNode(dom, htmlPage,
	 * timeElementName)方法来获取， 可以通过重写这两个方法来达到。
	 * 
	 * @param dom
	 * @param htmlPage
	 * @param sameHost
	 * @param className
	 * @param timeElementName
	 * @param timeElementName2
	 * @return
	 */
	public OfficialDocumentList getOfficialDocumentList(DomNode dom, HtmlPage htmlPage, boolean sameHost,
			String className, String timeElementName) {
		HtmlAnchor link = getLinkDomNode(dom, htmlPage);
		if (link != null) {
			String herf = getUrl(link, htmlPage, sameHost);
			if (!StringUtils.isEmpty(herf)) {
				DomNode time = getTimeDomNode(dom, htmlPage, timeElementName);
				OfficialDocumentList officialDocumentList = getOfficialDocumentList(herf, className, link);
				if (officialDocumentList != null) {
					if (time != null) {
						officialDocumentList.setTime(time.asText());
					}
				}
				return officialDocumentList;
			}
		}
		return null;
	}

	public String getUrl(DomNode node, HtmlPage htmlPage) {
		if (node instanceof HtmlAnchor) {
			return this.getUrl(node, htmlPage, false);
		}
		return null;
	}

	public String getUrl(DomNode node, HtmlPage htmlPage, Boolean sameHost) {
		if (node instanceof HtmlAnchor) {
			return this.getUrl((HtmlAnchor) node, htmlPage, sameHost);
		}
		return null;
	}

	public String getUrl(HtmlAnchor node, HtmlPage htmlPage) {
		return this.getUrl(node, htmlPage, false);
	}

	/**
	 * 
	 * @param node
	 * @param htmlPage
	 * @param sameHost 主要用于判断是否获取与htmlPage相同域名的node HtmlAnchor节点，
	 * @return
	 */
	public String getUrl(HtmlAnchor node, HtmlPage htmlPage, Boolean sameHost) {
		if (node == null || htmlPage == null) {
			return null;
		}
		HtmlAnchor domAttr = (HtmlAnchor) node;
		String href = domAttr.getHrefAttribute();
		return getUrl(href, htmlPage, sameHost);

	}

	public String getUrl(String href, HtmlPage htmlPage) {
		return getUrl(href, htmlPage, false);
	}

	/**
	 * * 获得相对路径的绝对路径网址。
	 * 
	 * @param href
	 * @param htmlPage
	 * @param sameHost主要用于判断是否获取与htmlPage相同域名的node HtmlAnchor节点，如果sameHost=true,href和htmlPage域名不相同，就返回null。
	 * 
	 * @return
	 */
	public String getUrl(String href, HtmlPage htmlPage, Boolean sameHost) {
		if (htmlPage == null) {
			return href;
		}
		try {
			URL url = htmlPage.getFullyQualifiedUrl(href);
			if (sameHost) {
				if (htmlPage.getUrl() != null) {
					String htmlPageHost = htmlPage.getUrl().getHost();
					if (!StringUtils.isEmpty(htmlPageHost)) {
						if (!htmlPageHost.equals(url.getHost())) {
							return null;
						}
					}
				}
			}
			return url.toString();
		} catch (MalformedURLException e1) {
			LOG.error("real href get error {}", href);
		}
		return null;
	}

	/**
	 * 传递一个baseUrl："http://www.sass.cn",在元素e中设置后获取href，再根据host判断， sameHost
	 * 
	 * @param e
	 * @param baseUrl
	 * @param sameHost
	 * @return
	 */
	public String getAbsUrl(Element e, String baseUrl, Boolean sameHost) {
		e.setBaseUri(baseUrl);
		String href = e.absUrl("href");
		if (sameHost) {
			try {
				URL url = new URL(href);
				URL base = new URL(baseUrl);
				if (!base.getHost().equals(url.getHost())) {
					href = null;
				}
			} catch (MalformedURLException e1) {
				LOG.error("abs href get error {}", href);
			}
		}
		return href;
	}

	/**
	 ** 该方法主要遍历文章中的附件
	 * 
	 * @param officialDocument
	 * @param htmlPage
	 * @param herfpath
	 * @return
	 */
	public OfficialDocument addattachments(OfficialDocument officialDocument, HtmlPage htmlPage, String herfpath) {
		if (htmlPage == null || officialDocument == null || StringUtils.isEmpty(herfpath)) {
			return officialDocument;
		}
		List<DomNode> attachment = htmlPage.getByXPath(herfpath);
		if (attachment != null && !attachment.isEmpty()) {
			List<String> attachments = new ArrayList<>();
			attachment.forEach(e -> {
				HtmlAnchor domAttr = (HtmlAnchor) e;
				String href = domAttr.getHrefAttribute();
				try {
					if (Util.isFile(href)) {
						href = htmlPage.getFullyQualifiedUrl(href).toString();
						attachments.add(href);
					} else {
						LOG.warn("{} unknown attachment file ", officialDocument.getUrl());
					}
				} catch (MalformedURLException e1) {
					LOG.error("real href get error {}", href);
				}

			});
			if (attachments != null && !attachments.isEmpty()) {
				List<String> oldMent = officialDocument.getAttachment();
				if (oldMent != null && oldMent.size() > 0 && !StringUtils.isEmpty(oldMent.get(0))) {
					attachments.addAll(oldMent);
				}
				officialDocument.setAttachment(attachments);
			}
		}
		return officialDocument;
	}



	/**
	 ** 该方法主要遍历文章中的附件
	 *
	 * @param officialDocument
	 * @param htmlPage
	 * @param herfpath
	 * @return
	 */


	public OfficialDocument addattachmentsWithOutEndCheck(OfficialDocument officialDocument, HtmlPage htmlPage,
			String herfpath) {
		if (htmlPage == null || officialDocument == null || StringUtils.isEmpty(herfpath)) {
			return officialDocument;
		}
		List<DomNode> attachment = htmlPage.getByXPath(herfpath);
		if (attachment != null && !attachment.isEmpty()) {
			List<String> attachments = new ArrayList<>();
			attachment.forEach(e -> {
				HtmlAnchor domAttr = (HtmlAnchor) e;
				String href = domAttr.getHrefAttribute();
				try {
					href = htmlPage.getFullyQualifiedUrl(href).toString();
					attachments.add(href);
				} catch (MalformedURLException e1) {
					LOG.error("real href get error {}", href);
				}
			});
			if (attachments != null && !attachments.isEmpty()) {
				List<String> oldMent = officialDocument.getAttachment();
				if (oldMent != null && oldMent.size() > 0 && !StringUtils.isEmpty(oldMent.get(0))) {
					attachments.addAll(oldMent);
				}
				officialDocument.setAttachment(attachments);
			}
		}
		return officialDocument;
	}

	public void addattachments(OfficialDocument officialDocument, String text, String baseUrl, boolean sameHost) {
		Document doc = Jsoup.parse(text);
		Elements listHerf = doc.getElementsByTag("a");
		if (listHerf != null) {
			List<String> attachments = new ArrayList<>();
			listHerf.forEach(e -> {
				String exml = e.outerHtml().toLowerCase();
				if (exml.contains("pdf") || exml.contains("doc") || exml.contains("docx") || exml.contains("xls")
						|| exml.contains("xlsx") || exml.contains("wps") || exml.contains("rtf")
						|| exml.contains("ceb")) {
					String href = getAbsUrl(e, baseUrl, sameHost);
					if (!StringUtils.isEmpty(href) && Util.isFile(href)) {
						attachments.add(href);
					}
				} else {
					LOG.warn("{} unknown attachment file ", officialDocument.getUrl());
				}

			});
			if (attachments != null && !attachments.isEmpty()) {
				List<String> oldMent = officialDocument.getAttachment();
				if (oldMent != null && oldMent.size() > 0 && !StringUtils.isEmpty(oldMent.get(0))) {
					attachments.addAll(oldMent);
				}
				officialDocument.setAttachment(attachments);
			}
		}
	}

	public Elements getElements(Document doc, String tagName) {
		if (doc == null) {
			return null;
		}
		Elements elements = doc.getElementsByTag(tagName);
		return elements;
	}

	public WebRequest getWebRequest(String url) throws MalformedURLException {
		return new WebRequest(new URL(url));
	}

	public WebRequest getProcessWebRequest(String url) throws MalformedURLException {
		return new WebRequest(UrlUtils.toUrlSafe(url));
	}

	public OfficialDocument addWebDriverAttachments(OfficialDocument officialDocument, WebDriver webDriver,
			List<WebElement> attachment) {
		if (officialDocument == null || attachment == null) {
			return officialDocument;
		}
		if (attachment != null && !attachment.isEmpty()) {
			List<String> attachments = new ArrayList<>();
			attachment.forEach(e -> {
				String href = e.getAttribute("href");
				if (Util.isFile(href)) {
					attachments.add(href);
				} else {
					LOG.warn("{} unknown attachment file ", officialDocument.getUrl());
				}
			});
			if (attachments != null && !attachments.isEmpty()) {
				List<String> oldMent = officialDocument.getAttachment();
				if (oldMent != null && oldMent.size() > 0 && !StringUtils.isEmpty(oldMent.get(0))) {
					attachments.addAll(oldMent);
				}
				officialDocument.setAttachment(attachments);
			}
		}
		return officialDocument;
	}

	protected <T> T waitByCondition(WebDriver webDriver, ExpectedCondition<T> expectedCondition,
			long timeOutInSeconds) {
		return new WebDriverWait(webDriver, timeOutInSeconds).until(expectedCondition);

	}

	public Charset getChareset(String urlStr) {
		try {
			return this.getChareset(UrlUtils.toUrlUnsafe(urlStr));
		} catch (MalformedURLException e) {
			LOG.error("网页地址格式有误，无法转换为URL");
		}
		return null;
	}

	public Charset getChareset(URL url) {
		Charset charset = Charset.forName("UTF-8");
		try {
			String[] probableSet = CharsetDetector.detectChineseCharset(url.openStream());
			if (probableSet != null && probableSet.length > 0) {
				charset = Charset.forName(probableSet[0]);
			}
		} catch (IOException e) {
			LOG.error("未找到请求网页字符编码，返回默认编码UTF-8");
		}
		return charset;

	}

	public Page ChangePageCharSet(Page page) {
		WebResponse webResponse = page.getWebResponse();
		StringWebResponse response = null;
		Charset charset = webResponse.getContentCharsetOrNull();
		if (charset != null && charset.equals(Charset.forName("gb2312"))) {
			charset = Charset.forName("GBK");
		} else {
			return page;
		}
		try (InputStream is = webResponse.getContentAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String str = sb.toString();
			response = new StringWebResponse(str, new URL(page.getUrl().toString()));
			if (page instanceof HtmlPage) {
				return HTMLParser.parseHtml(response, ((HtmlPage) page).getWebClient().getCurrentWindow());
			} else if (page instanceof XmlPage) {
				HTMLParser.parseXHtml(response, ((XmlPage) page).getWebClient().getCurrentWindow());
				return HTMLParser.parseHtml(response, ((HtmlPage) page).getWebClient().getCurrentWindow());
			} else if (page instanceof TextPage) {
				/** 未测试webWindow */
				WebWindow webWindow = ((TextPage) page).getEnclosingWindow();
				final TextPage textPage = new TextPage(response, ((TextPage) page).getEnclosingWindow());
				webWindow.setEnclosedPage(textPage);
				return textPage;
			} else {
				/** 未测试webWindow */
				WebWindow webWindow = page.getEnclosingWindow();
				final UnexpectedPage unexpectedPage = new UnexpectedPage(response, webWindow);
				webWindow.setEnclosedPage(unexpectedPage);
				return unexpectedPage;

			}
		} catch (IOException e) {
			LOG.error("{} ChangePageCharSet wrong {}", page.getUrl(), e.getMessage());
		} finally {
			webResponse.cleanUp();
			response.cleanUp();
		}
		return page;

	}
	public Page changeUnexpectedPage(Page o) {
		if (o!=null&&o instanceof UnexpectedPage
				&& "application/json".equals(o.getWebResponse().getContentType())) {
			try {
				o = HTMLParser.parseHtml(o.getWebResponse(), o.getEnclosingWindow());
			} catch (IOException e) {
				LOG.error("{} 请求网页UnexpectedPage {}", o.getUrl(),e.getMessage());
			}
		}
		return o;

	}
}
