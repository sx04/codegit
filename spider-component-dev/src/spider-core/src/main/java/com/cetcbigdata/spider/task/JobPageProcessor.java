package com.cetcbigdata.spider.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.spider.entity.Attachment;
import com.cetcbigdata.spider.entity.Img;
import com.cetcbigdata.spider.entity.OfficialDocument;
import com.cetcbigdata.spider.entity.OfficialDocumentList;
import com.cetcbigdata.spider.factory.SnowflakeIdWorker;
import com.cetcbigdata.spider.util.Util;
import com.cetcbigdata.spider.work.HttpClientPool;
import com.cetcbigdata.spider.work.ITask;
import com.cetcbigdata.spider.work.SaveData;
import com.cetcbigdata.spider.work.WebClientPool;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.xxl.job.core.biz.model.ReturnT;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author matthew
 */
@Component
public class JobPageProcessor extends BaseJobPageHandler implements ITask {

	@Autowired
	RedisTemplate redisTemplate;
	@Autowired
	MongoTemplate mongoTemplatet;
	@Autowired
	WebClientPool webClientPool;
	@Autowired
	HttpClientPool httpClientPool;
	@Value("${crawler.down.path}")
	private String downPath;

	@Autowired
	private SaveData saveData;

	SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

	Boolean needProxy = false;

	private static final Logger LOG = LoggerFactory.getLogger(JobPageProcessor.class);

	@Override
	public ReturnT<String> execute(String s) throws Exception {
		return null;
	}

	@Override
	void createIndex(String collectionName) {
		if (!mongoTemplatet.collectionExists(collectionName)) {
			mongoTemplatet.createCollection(collectionName);
			DBObject indexOptions = new BasicDBObject();
			indexOptions.put("url", 1);
			mongoTemplatet.getCollection(collectionName).createIndex(indexOptions, "url", true);
		}

	}

	@Override
	void saveData(OfficialDocument officialDocument, String collectionName) {
		if (!mongoTemplatet.collectionExists(collectionName)) {
			this.createIndex(collectionName);
		}
		saveData.saveData(officialDocument, collectionName);
	}

	@Override
	void returnQueue(String taskQueue, OfficialDocumentList officialDocumentList) {
		if (officialDocumentList != null) {
			redisTemplate.opsForList().leftPush(taskQueue, officialDocumentList);
		}
	}

	@Override
	void exceptionWebHandler(String taskQueue, OfficialDocumentList officialDocumentList, ITask iTask, Boolean proxy) {
		LOG.info("switch proxy {}", iTask.getClass());
		returnQueue(taskQueue, officialDocumentList);
	}

	@Override
	OkHttpClient exceptionHttpHandler(String taskQueue, OfficialDocumentList officialDocumentList, ITask iTask) {
		LOG.info("switch proxy {}", iTask.getClass());
		returnQueue(taskQueue, officialDocumentList);
		return httpClientPool.getFromPool();
	}

	@Override
	void exceptionWebDriverHandler(String taskQueue, OfficialDocumentList officialDocumentList, ITask iTask) {
		LOG.info("switch proxy {}", iTask.getClass());
		returnQueue(taskQueue, officialDocumentList);
	}

	@Override
	public void process() throws Exception {

	}

	@SuppressWarnings("unchecked")
	Object popObject(String taskQueue) {
		Object o = redisTemplate.opsForList().rightPop(taskQueue);
		if (o == null) {
			int i = 0;
			while (i < 5 && o == null) {
				i++;
				o = redisTemplate.opsForList().rightPop(taskQueue);
			}
		}
		return o;
	}

	Object getValue(String key) {
		Object o = redisTemplate.opsForValue().get(key);
		if (o == null) {
			int i = 0;
			while (i < 5 && o == null) {
				i++;
				o = redisTemplate.opsForValue().get(key);
			}
		}
		return o;
	}

	void addQueue(String taskQueue, OfficialDocumentList officialDocumentList) {
		saveData.addQueue(taskQueue, officialDocumentList);
	}

	@SuppressWarnings("unchecked")
	public void saveValue(String key, Object value, long timeout) {
		if (!StringUtils.isEmpty(key)) {
			redisTemplate.delete(key);
			redisTemplate.opsForValue().set(key, value);
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);

		}
	}

	public boolean taskIsEmpty(String taskQueue) {
		Set<String> keySet = redisTemplate.keys(taskQueue);
		if (keySet == null) {
			return true;
		}
		return keySet.isEmpty();
	}

	WebClient getWebclient(Boolean needProxy) {
		return webClientPool.getFromPool(needProxy);
	}

	public void getContentList(ThreadLocal<WebClient> webClient, String url, String className, String taskQueue,
			String herfpath) throws MalformedURLException {
		WebRequest webRequest = getWebRequest(url);
		getContentList(webClient, className, webRequest, taskQueue, needProxy, herfpath, null, false);
	}

	public void getContentList(ThreadLocal<WebClient> webClient, String className, WebRequest webRequest,
			String taskQueue, Boolean needProxy, String herfpath, String timeElementName, Boolean sameHost) {
		this.getContentList(webClient, className, webRequest, taskQueue, needProxy, herfpath, timeElementName, sameHost,
				null);
	}

	/**
	 * 
	 * @param webClient
	 * @param className
	 * @param webRequest
	 * @param taskQueue
	 * @param needProxy
	 * @param herfpath
	 * @param timeElementName
	 * @param sameHost        默认传false,表示不用筛查不在同一个域名的文章
	 */
	public void getContentList(ThreadLocal<WebClient> webClient, String className, WebRequest webRequest,
			String taskQueue, Boolean needProxy, String herfpath, String timeElementName, Boolean sameHost,
			Charset charset) {
		HtmlPage htmlPage = getHtmlPageCount(webClient, webRequest, needProxy);
		if (htmlPage != null) {
			List<DomNode> domNodes = htmlPage.getByXPath(herfpath);
			addQueueByDomNodeList(domNodes, className, htmlPage, taskQueue, timeElementName, sameHost);
		}
	}

	/**
	 * * 主要通过webRequest来获得一个HtmlPage。
	 * 
	 * @param webClient
	 * @param webRequest
	 * @param needProxy
	 * @return
	 */
	public HtmlPage getHtmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest, Boolean needProxy) {
		try {
			if (webClient.get() == null) {
				WebClient webClient1 = getWebclient(needProxy);
				webClient.set(webClient1);
			}
			LOG.info("start spider url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage() || o.getWebResponse().getStatusCode() == 403
					|| o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} not a HTML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else {
				o = ChangePageCharSet(o);
				HtmlPage htmlPage = (HtmlPage) o;
				return htmlPage;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", webRequest.getUrl());
			} else {
				LOG.error("{} url unknow error {}", webRequest.getUrl(), e.getMessage());
			}
		} finally {
			if (!needProxy) {
				webClient.remove();
			}
		}
		return null;
	}

	private HtmlPage getHtmlPageCount(ThreadLocal<WebClient> webClient, WebRequest request, Boolean needProxy2) {
		HtmlPage htmlPage = null;
		int count = 0;
		do {
			htmlPage = getHtmlPage(webClient, request, needProxy);
			if (count >= 3 || htmlPage != null) {
				break;
			}
			count++;
		} while (true);
		return htmlPage;
	}

	public HtmlPage getProcessHtmlPage(ThreadLocal<WebClient> webClient, String url, ITask className,
			OfficialDocumentList officialDocumentList, String taskQueue, Boolean needProxy) {
		try {
			WebRequest request = getProcessWebRequest(url);
			return this.getProcessHtmlPage(webClient, request, className, officialDocumentList, taskQueue, needProxy);
		} catch (MalformedURLException e) {
			LOG.error("{} MalformedURLException error {}", url, e.getMessage());
		}
		return null;

	}

	/**
	 ** 主要用于抓取文章内容，如果文章是附件，就单独存放，如果访问超时，就重新放入redids队列中，并设置officialDocumentList的抓取次数。
	 * 
	 * @param webClient
	 * @param url
	 * @param className
	 * @param officialDocumentList
	 * @param taskQueue
	 * @param needProxy
	 * @return
	 * @throws InterruptedException
	 */

	public HtmlPage getProcessHtmlPage(ThreadLocal<WebClient> webClient, WebRequest request, ITask className,
			OfficialDocumentList officialDocumentList, String taskQueue, Boolean needProxy) {
		String url = request.getUrl().toString();
		try {
			if (webClient.get() == null) {
				WebClient webClient1 = getWebclient(needProxy);
				webClient.set(webClient1);
			}
			Page o = webClient.get().getPage(request);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage()) {
				LOG.info("not a HTML PAGE");
				if (Util.isFile(officialDocumentList.getHref())
						|| Util.isFileByResponseType(o.getWebResponse().getContentType())) {
					LOG.info("this is a file");
					saveAttachment(officialDocumentList);
					return null;
				}
				LOG.warn("no content to crawler {}", officialDocumentList.getHref());
				return null;
			} else if (o.getWebResponse().getStatusCode() == 403 || o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} Page Not Found {}", request.getUrl(), o.getWebResponse());
				return null;
			}
			LOG.info("开始抓取url: {}", url);
			o = ChangePageCharSet(o);
			HtmlPage htmlPage = (HtmlPage) o;
			return htmlPage;
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				int count = officialDocumentList.getCrawlerCount();
				officialDocumentList.setCrawlerCount(++count);
				LOG.error("connection  error {},{}", url, e.getMessage());
				exceptionWebHandler(taskQueue, officialDocumentList, className, needProxy);
			} else {
				LOG.error("{} url unknow error {}", url, e.getMessage());
			}
		} finally {
			if (!needProxy) {
				webClient.remove();
			} else {
				webClientPool.returnToPool(webClient.get());
			}
		}
		return null;
	}

	public XmlPage getXmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest, Boolean needProxy) {
		try {
			if (webClient.get() == null) {
				WebClient webClient1 = getWebclient(needProxy);
				webClient.set(webClient1);
			}
			LOG.info("start spider url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			if (o instanceof XmlPage) {
				o = ChangePageCharSet(o);
				return (XmlPage) o;
			} else {
				LOG.info("{} not a XML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", webRequest.getUrl());
			} else {
				LOG.error("{} url unknow error {}", webRequest.getUrl(), e.getMessage());
			}
		} finally {
			if (!needProxy) {
				webClient.remove();
			}
		}
		return null;
	}

	public XmlPage getProcessXmlPage(ThreadLocal<WebClient> webClient, String url, ITask className,
			OfficialDocumentList officialDocumentList, String taskQueue, Boolean needProxy)
			throws InterruptedException {
		try {
			if (webClient.get() == null) {
				WebClient webClient1 = getWebclient(needProxy);
				webClient.set(webClient1);
			}
			WebRequest webRequest = getProcessWebRequest(url);
			Page o = webClient.get().getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (o instanceof XmlPage) {
				LOG.info("开始抓取url: {}", url);
				o = ChangePageCharSet(o);
				XmlPage xmlPage = (XmlPage) o;
				return xmlPage;
			} else {
				LOG.info("not a Xml PAGE");
				if (Util.isFile(url) || Util.isFileByResponseType(o.getWebResponse().getContentType())) {
					LOG.info("this is a file");
					saveAttachment(officialDocumentList);
					return null;
				} else if (o.getWebResponse().getStatusCode() == 403 || o.getWebResponse().getStatusCode() == 500) {
					LOG.info("{} Page Not Found {}", url, o.getWebResponse());
					return null;
				}
				LOG.warn("no content to crawler {}", url);
				return null;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				int count = officialDocumentList.getCrawlerCount();
				officialDocumentList.setCrawlerCount(++count);
				LOG.error("connection  error {},{}", url, e.getMessage());
				exceptionWebHandler(taskQueue, officialDocumentList, className, needProxy);
			} else {
				LOG.error("{} url unknow error {}", url, e.getMessage());
			}
		} finally {
			if (!needProxy) {
				webClient.remove();
			} else {
				webClientPool.returnToPool(webClient.get());
			}
		}
		return null;
	}

	/**
	 * timeElementName 主要用于设置时间参数的标签值，在这种情况下domNodeList 需要是一个a标签和时间标签的上一级标签， 如
	 * <li><span>2018-06-08</span><a
	 * href=".../srcsite/A06/jcys_jyzb/201806/t20180607_338712.html" target="_blank"
	 * title="《中小学图书馆(室)规程》的通知</a></li>
	 ** 该li标签中有时间span标签和a标签，这个时候如果需要把时间也给OfficialDocumentList，就需要domNodeList中传送的是li标签，以执行dom.hasChildNodes()的方法，
	 ** 再通过getTimeDomNode（)方法来获得时间标签span
	 * 
	 * 如果不满足上述条件，可以通过重写getLinkDomNode(dom, htmlPage)和getTimeDomNode(dom, htmlPage,
	 * timeElementName)方法来获取。
	 * 
	 * @param domNodeList
	 * @param className
	 * @param htmlPage
	 * @param taskQueue
	 * @param timeElementName
	 * @param herfpath
	 * @param sameHost
	 */
	public void addQueueByDomNodeList(List<DomNode> domNodeList, String className, HtmlPage htmlPage, String taskQueue,
			String timeElementName, Boolean sameHost) {
		if (domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return;
		}
		domNodeList.parallelStream().forEach(dom -> {
			if (dom != null) {
				if (dom instanceof HtmlAnchor) {
					OfficialDocumentList officialDocumentList = getOfficialDocumentList(dom, htmlPage, sameHost,
							className);
					if (officialDocumentList != null) {
						addQueue(taskQueue, officialDocumentList);
					}
				} else if (dom.hasChildNodes()) {
					OfficialDocumentList officialDocumentList = getOfficialDocumentList(dom, htmlPage, sameHost,
							className, timeElementName);
					if (officialDocumentList != null) {
						addQueue(taskQueue, officialDocumentList);
					}

				}
			}
		});
	}

	/**
	 * 该方法主要通过遍历个分页获取url，然后传递给redis队列。分页中，通过循环判断domNodeList是否有值来判断页面是否有下一页。
	 * 
	 * @param index
	 * @param pageHref
	 * @param webClient
	 * @param herfpath
	 * @param needProxy
	 * @param taskQueue
	 * @param className
	 * @param startNum        用于首页的起始数字传递。
	 * @param timeElementName
	 * @param timeElementName
	 * @param sameHost
	 */

	public void getPageListAddQueue(String index, String pageHref, ThreadLocal<WebClient> webClient,
			String suprherfpath, Boolean needProxy, String taskQueue, String className, int startNum,
			String timeElementName, Boolean sameHost) {
		String url = "";
		WebRequest request = null;
		HtmlPage oldHtmlPage = null;
		int i = startNum;
		do {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = String.format(pageHref, i);
				}

				request = getWebRequest(url);
				HtmlPage htmlPage = getHtmlPageCount(webClient, request, needProxy);

				if (htmlPage == null && oldHtmlPage == null) {
					LOG.info("{}分页请求结束", url);
					break;
				} else if (htmlPage != null && oldHtmlPage != null) {
					if (htmlPage.asText().replace(i + "", "").replace(i - 1 + "", "")
							.equals(oldHtmlPage.asText().replace(i + "", "").replace(i - 1 + "", ""))) {
						LOG.info("{}分页请求结束", url);
						break;
					}
				}
				i++;
				List<DomNode> domNodeList = super.getDomNodeList(htmlPage, suprherfpath);

				oldHtmlPage = htmlPage;
				if (domNodeList == null || domNodeList.size() <= 0) {
					LOG.error("从{}根据xpth{}未获取到文章列表", url, suprherfpath);
				} else {
					addQueueByDomNodeList(domNodeList, className, htmlPage, taskQueue, timeElementName, sameHost);
				}

			} catch (Exception e) {
				LOG.error("{} getPageListAddQueue while error {}", url, e.getMessage());
			} finally {
			}
		} while (true);

	}

	/**
	 * 传递一个页码数量下，调用改方法，用for循环进行遍历
	 * 
	 * @param index
	 * @param pageHref
	 * @param webClient
	 * @param herfpath
	 * @param needProxy
	 * @param taskQueue
	 * @param className
	 * @param pageNum
	 * @param timeElementName
	 * @param sameHost
	 */
	public void getPageList(String index, String pageHref, ThreadLocal<WebClient> webClient, String herfpath,
			Boolean needProxy, String taskQueue, String className, int startNum, int pageNum, String timeElementName,
			Boolean sameHost) {
		String url = "";
		WebRequest request = null;
		HtmlPage htmlPage = null;
		List<DomNode> domNodeList = null;
		int sum = pageNum + startNum;
		for (int i = startNum; i < sum; i++) {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = String.format(pageHref, i);
				}
				request = getWebRequest(url);
				htmlPage = getHtmlPageCount(webClient, request, needProxy);
				domNodeList = super.getDomNodeList(htmlPage, herfpath);
				if (domNodeList == null || domNodeList.size() <= 0) {
					LOG.error("从{}根据xpth{}未获取到文章列表", url, herfpath);
				} else {
					addQueueByDomNodeList(domNodeList, className, htmlPage, taskQueue, timeElementName, sameHost);
				}
			} catch (Exception e) {
				LOG.error("{} getPageList for error {}", url, e.getMessage());
			}
		}
	}

	public void getXmlPageList(String index, String pageHref, ThreadLocal<WebClient> webClient, String timeTag,
			Boolean needProxy, String taskQueue, String className, int startNum, int pageNum, String baeUrl,
			String tagName, boolean sameHost) {
		String url = "";
		WebRequest request = null;
		XmlPage xmlPage = null;
		int sum = pageNum + startNum;
		for (int i = startNum; i < sum; i++) {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = String.format(pageHref, i);
				}
				request = getWebRequest(url);
				xmlPage = getXmlPage(webClient, request, needProxy);
				disposeXmlPage(xmlPage, tagName, timeTag, baeUrl, sameHost, className, taskQueue);

			} catch (Exception e) {
				LOG.error("{} getPageList for error {}", url, e.getMessage());
			}
		}
	}

	/**
	 * 处理xml格式网页，通过得到doc，并遍历tagName标签内容，然后循环得到OfficialDocumentList，再提交给任务taskQueue
	 * 
	 * @param xmlPage
	 * @param tagName
	 * @param baeUrl
	 * @param sameHost
	 * @param className
	 * @param taskQueue
	 */
	public void disposeXmlPage(XmlPage xmlPage, String tagName, String timeTag, String baeUrl, boolean sameHost,
			String className, String taskQueue) {
		Document doc = getDocument(xmlPage.asXml());
		Elements elements = getElements(doc, tagName);
		if (elements == null) {
			LOG.error("从{}根据tagName{}未获取到文章列表", xmlPage.getUrl(), tagName);
			return;
		}
		elements.forEach(e -> {
			/** 如果tagName 是a标签 **/
			if ("a".equals(tagName)) {
				OfficialDocumentList officialDocumentList = getOfficialDocumentList(e, baeUrl, sameHost, className);
				if (officialDocumentList != null) {
					addQueue(taskQueue, officialDocumentList);
				}
			} else {
				/** 如果tagName 不是a标签， 就需要getHrefAndTimeOfficialDocumentList去获取a标签和时间标签 **/
				OfficialDocumentList officialDocumentList = getHrefAndTimeOfficialDocumentList(e, timeTag, baeUrl,
						sameHost, className);
				if (officialDocumentList != null) {
					addQueue(taskQueue, officialDocumentList);
				}
			}

		});

	}

	/**
	 * 默认a标签第一个是网址，timeTag第一个是时间标签。否则，需要重写此方法。
	 * 
	 * @param e
	 * @param timeTag
	 * @param baeUrl
	 * @param sameHost
	 * @param className
	 * @return
	 */
	public OfficialDocumentList getHrefAndTimeOfficialDocumentList(Element e, String timeTag, String baeUrl,
			boolean sameHost, String className) {
		Elements as = e.getElementsByTag("a");
		Elements times = e.getElementsByTag(timeTag);

		if (as.size() > 0) {
			Element a = as.get(0);
			OfficialDocumentList officialDocumentList = getOfficialDocumentList(a, baeUrl, sameHost, className);
			if (officialDocumentList != null) {
				if (times.size() > 0) {
					officialDocumentList.setTime(times.get(0).text());
				}
			}
			return officialDocumentList;
		}
		return null;

	}

	/**
	 * 将字符串转换为网页Document；此方法可以通过子类重写来根据实际情况修改代码得到需要的内容。
	 * 
	 * @param pageContent
	 * @return
	 */
	public Document getDocument(String pageContent) {
		return Jsoup.parse(pageContent);
	}

	/**
	 * 获取格式化的url地址；可以通过方法重写修改内容
	 * 
	 * @param pageHref
	 * @param i
	 * @return
	 */
	public String getFormatUrl(String pageHref, int i) {
		return String.format(pageHref, i);
	}

	/**
	 * 用while循环处理get请求得到的xml网页，页面参数传递方式有两种，本方法实现第一种
	 * 通过url地址传递页面参数，可以通过getFormatUrl(pageHref,i++)来实现；
	 * 
	 * 
	 * 如果需要获取时间值，tagName就不能是a标签，且timeTag就是要获取的时间元素名。详情请见方法getHrefAndTimeOfficialDocumentList（）
	 * 
	 * @param webClient
	 * @param pageHref
	 * @param nameValuePairs
	 * @param taskQueue
	 * @param className
	 * @param needProxy
	 * @param tagName
	 * @param baeUrl
	 * @param sameHost
	 */
	public void getXmlPageWhileUrl(ThreadLocal<WebClient> webClient, String index, String pageHref, String taskQueue,
			String className, Boolean needProxy, String tagName, String timeTag, String baeUrl, int startNum,
			boolean sameHost) {
		XmlPage xmlPageOld = null;
		int i = startNum;
		String url = "";
		do {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = getFormatUrl(pageHref, i);
				}
				WebRequest request = new WebRequest(new URL(url), HttpMethod.GET);
				XmlPage xmlPage = getXmlPage(webClient, request, needProxy);
				/**
				 * 如果得到的网页是空，避免死循环，应该即可结束循环
				 */
				if (xmlPage == null && xmlPageOld == null) {
					LOG.info("{}分页请求结束", url);
					break;
					/**
					 * 如果上一次请求和本次请求的内容相同，认为访问的页面是最后一页，或者最后的错误一页，应该结束循环
					 */
				} else if (xmlPage != null && xmlPageOld != null) {
					if (xmlPage.asText().replace(i + "", "").replace(i - 1 + "", "")
							.equals(xmlPageOld.asText().replace(i + "", "").replace(i - 1 + "", ""))) {
						LOG.info("{}分页请求结束", url);
						break;
					}
				}
				i++;
				xmlPageOld = xmlPage;
				disposeXmlPage(xmlPage, tagName, timeTag, baeUrl, sameHost, className, taskQueue);
			} catch (MalformedURLException e) {
				LOG.error("{} url  MalformedURLException  {}", url, e.getMessage());
			}
		} while (true);

	}

	/**
	 * 用while循环处理post请求得到的xml网页，页面参数传递方式有两种，本方法实现第一种
	 * 通过url地址传递页面参数，可以通过getFormatUrl(pageHref,i++)来实现；
	 * 方法二，如果不是通过url地址传递页面参数，而是通过post参数，可以修改nameValuePairs来实现不同页面的请求循环。
	 * 
	 * 如果需要获取时间值，tagName就不能是a标签，且timeTag就是要获取的时间元素名。详情请见方法getHrefAndTimeOfficialDocumentList（）
	 * 
	 * @param webClient
	 * @param pageHref
	 * @param nameValuePairs
	 * @param taskQueue
	 * @param className
	 * @param needProxy
	 * @param tagName
	 * @param timeTag
	 * @param baeUrl
	 * @param sameHost
	 */
	public void postXmlPageWhileUrl(ThreadLocal<WebClient> webClient, String index, String pageHref,
			List<NameValuePair> nameValuePairs, String taskQueue, String className, Boolean needProxy, String tagName,
			String timeTag, String baeUrl, int startNum, boolean sameHost) {
		XmlPage xmlPageOld = null;
		int i = startNum;
		String url = "";
		do {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = getFormatUrl(pageHref, i);
				}
				WebRequest request = new WebRequest(new URL(url), HttpMethod.POST);
				request.setRequestParameters(nameValuePairs);
				XmlPage xmlPage = getXmlPage(webClient, request, needProxy);
				/**
				 * 如果得到的网页是空，避免死循环，应该即可结束循环
				 */
				if (xmlPage == null && xmlPageOld == null) {
					LOG.info("{}分页请求结束", url);
					break;
					/**
					 * 如果上一次请求和本次请求的内容相同，认为访问的页面是最后一页，或者最后的错误一页，应该结束循环
					 */
				} else if (xmlPage != null && xmlPageOld != null) {
					if (xmlPage.asText().replace(i + "", "").replace(i - 1 + "", "")
							.equals(xmlPageOld.asText().replace(i + "", "").replace(i - 1 + "", ""))) {
						LOG.info("{}分页请求结束", url);
						break;
					}
				}
				i++;
				xmlPageOld = xmlPage;
				disposeXmlPage(xmlPage, tagName, timeTag, baeUrl, sameHost, className, taskQueue);
			} catch (MalformedURLException e) {
				LOG.error("{} url  MalformedURLException  {}", url, e.getMessage());
			}
		} while (true);

	}

	/**
	 * post方式获取列表，用do-while循环，post页面参数注意通过pageKey来传递，需要注意的是，页面参数不能重复，
	 * 
	 * @param index
	 * @param herfpath
	 * @param nameValuePairs
	 * @param startNum
	 * @param pageKey
	 * @param className
	 * @param taskQueue
	 * @param timeElementName
	 * @param sameHost
	 */

	public void postPageListWhile(ThreadLocal<WebClient> webClient, String index, String herfpath,
			List<NameValuePair> nameValuePairs, Integer startNum, String pageKey, String className, String taskQueue,
			String timeElementName, Boolean sameHost) {
		try {
			WebRequest requestSettings = new WebRequest(new URL(index), HttpMethod.POST);
			HtmlPage oldHtmlPage = null;
			int i = startNum;
			do {
				// 删除以前的页码参数
				nameValuePairs.remove(new NameValuePair(pageKey, "" + (i - 1)));
				nameValuePairs.add(new NameValuePair(pageKey, "" + i));
				requestSettings.setRequestParameters(nameValuePairs);
				HtmlPage htmlPage = getHtmlPageCount(webClient, requestSettings, needProxy);
				/**
				 * 如果得到的网页是空，避免死循环，应该即可结束循环
				 */
				if (htmlPage == null && oldHtmlPage == null) {
					LOG.info("{}页码{}分页请求结束", index, i);
					break;
					/**
					 * 如果上一次请求和本次请求的内容相同，认为访问的页面是最后一页，或者最后的错误一页，应该结束循环
					 */
				} else if (htmlPage != null && oldHtmlPage != null) {
					if (htmlPage.asText().replace(i + "", "").replace(i - 1 + "", "")
							.equals(oldHtmlPage.asText().replace(i + "", "").replace(i - 1 + "", ""))) {
						LOG.info("{}页码{}分页请求结束", index, i);
						break;
					}
				}
				i++;
				oldHtmlPage = htmlPage;
				if (htmlPage != null) {
					List<DomNode> domNodeList = htmlPage.getByXPath(herfpath);
					if (domNodeList == null || domNodeList.size() <= 0) {
						LOG.error("从{}根据xpth{}发送post请求第{}页未获取到文章列表", index, herfpath, i);
					} else {
						addQueueByDomNodeList(domNodeList, className, htmlPage, taskQueue, timeElementName, sameHost);
					}
				}
			} while (true);
		} catch (MalformedURLException e) {
			LOG.error("{} url  MalformedURLException  {}", index, e.getMessage());
		}

	}

	/**
	 * post方式获取列表，用for循环，post页面参数注意通过pageKey来传递，需要注意的是，页面参数不能重复，
	 * 
	 * @param webClient
	 * @param index
	 * @param herfpath
	 * @param nameValuePairs
	 * @param startNum
	 * @param pageKey
	 * @param className
	 * @param taskQueue
	 * @param timeElementName
	 * @param sameHost
	 */
	public void postPageListFor(ThreadLocal<WebClient> webClient, String index, String herfpath,
			List<NameValuePair> nameValuePairs, Integer startNum, Integer pagSum, String pageKey, String className,
			String taskQueue, String timeElementName, Boolean sameHost) {
		try {
			WebRequest requestSettings = new WebRequest(new URL(index), HttpMethod.POST);
			List<DomNode> domNodeList = null;
			Integer sum = pagSum + startNum;
			for (int i = startNum; i < sum; i++) {
				// 删除以前的页码参数
				nameValuePairs.remove(new NameValuePair(pageKey, "" + (i - 1)));
				nameValuePairs.add(new NameValuePair(pageKey, "" + i));
				requestSettings.setRequestParameters(nameValuePairs);
				HtmlPage htmlPage = getHtmlPageCount(webClient, requestSettings, needProxy);
				if (htmlPage != null) {
					domNodeList = htmlPage.getByXPath(herfpath);
					if (domNodeList == null || domNodeList.size() <= 0) {
						LOG.error("从{}根据xpth{}发送post请求第{}页未获取到文章列表", index, herfpath, i);
					} else {
						addQueueByDomNodeList(domNodeList, className, htmlPage, taskQueue, timeElementName, sameHost);
					}
				}
			}
		} catch (MalformedURLException e) {
			LOG.error("{} url  error  {}", index, e.getMessage());
		}

	}

	public void getPageListForWebDrive(String index, String pageHref, WebDriver webDriver, String herfpath,
			String taskQueue, String className, int startNum, int pageNum) {
		String url = "";
		int sum = pageNum + startNum;
		for (int i = startNum; i < sum; i++) {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = String.format(pageHref, i);
				}
				List<WebElement> elements = getContentListByWebDriver(webDriver, url, herfpath, taskQueue, className);
				if (elements != null && elements.size() > 0) {
					getWebDriverListAddQueue(elements, herfpath, taskQueue, className);
				} else {
					LOG.error("从{}根据xpth{}未获取到文章列表", url, herfpath);
				}
			} catch (Exception e) {
				LOG.error("{} getPageList for error {}", url, e.getMessage());
			}
		}
	}

	public void getPageListWhileWebDrive(String index, String pageHref, WebDriver webDriver, String herfpath,
			String taskQueue, String className, int startNum) {
		String url = "";
		int i = startNum;
		List<WebElement> elementsOld = null;
		do {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = getFormatUrl(pageHref, i);
				}
				i++;
				List<WebElement> elements = getContentListByWebDriver(webDriver, url, herfpath, taskQueue, className);
				if (elements == null && elementsOld == null) {
					LOG.info("{}分页请求结束", url);
					break;
				} else if (elements != null && elementsOld != null) {
					if (elements.equals(elementsOld)) {
						LOG.info("{}分页请求结束", url);
						break;
					}
				}
				if (elements != null && elements.size() > 0) {
					getWebDriverListAddQueue(elements, herfpath, taskQueue, className);
				} else {
					LOG.error("从{}根据xpth{}未获取到文章列表", url, herfpath);
				}
				elementsOld = elements;
			} catch (Exception e) {
				LOG.error("{} getPageList for error {}", url, e.getMessage());
			}
		} while (true);

	}

	public List<WebElement> getContentListByWebDriver(WebDriver webDriver, String url, String herfpath,
			String taskQueue, String className) {
		List<WebElement> elements = null;
		try {
			webDriver.get(url);
			elements = webDriver.findElements(By.xpath(herfpath));
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", url);
			} else {
				LOG.error("{} url unknow error {}", url, e.getMessage());
			}
		}
		return elements;
	}

	public void getWebDriverListAddQueue(List<WebElement> elements, String herfpath, String taskQueue,
			String className) {
		elements.forEach(e -> {
			String title = e.getAttribute("title");
			if (StringUtils.isBlank(title)) {
				title = e.getText();
			}
			String name = title;
			String href = e.getAttribute("href");
			OfficialDocumentList officialDocumentList = new OfficialDocumentList();
			officialDocumentList.setHref(href);
			officialDocumentList.setName(name);
			officialDocumentList.setTaskClass(className);
			officialDocumentList.setCrawlerCount(0);
			addQueue(taskQueue, officialDocumentList);
		});

	}

	public WebDriver getProcessWebDriver(WebDriver webDriver, String url, ITask iTask,
			OfficialDocumentList officialDocumentList, String taskQueue) {
		try {
			webDriver.get(url);
			if (Util.isFile(officialDocumentList.getHref())) {
				LOG.info("this is a file");
				saveAttachment(officialDocumentList);
				return null;
			}
			LOG.info("开始抓取url: {}", url);
			return webDriver;
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				int count = officialDocumentList.getCrawlerCount();
				officialDocumentList.setCrawlerCount(++count);
				LOG.error("connection  error {},{}", url, e.getMessage());
				exceptionWebDriverHandler(taskQueue, officialDocumentList, iTask);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param index
	 * @param webDriver
	 * @param herfpath     列表页面的参数
	 * @param nextPagePath 下一页的xpth参数
	 * @param taskQueue
	 * @param className
	 */
	public void clickPageListForWebDrive(String index, WebDriver webDriver, String herfpath, String nextPagePath,
			String taskQueue, String className) {
		webDriver.get(index);
		WebElement element = null;
		List<WebElement> oldList = null;
		do {
			try {
				ExpectedCondition<List<WebElement>> condition = ExpectedConditions
						.visibilityOfAllElementsLocatedBy(By.xpath(nextPagePath));
				waitByCondition(webDriver, condition, 10);
				element = webDriver.findElement(By.xpath(nextPagePath));
				List<WebElement> currentList = webDriver.findElements(By.xpath(herfpath));
				if (currentList == null || currentList.equals(oldList)) {
					break;
				}
				if (currentList == null && oldList == null) {
					LOG.info("{}分页请求结束", index);
					break;
				} else if (currentList != null && oldList != null) {
					if (currentList.equals(oldList)) {
						LOG.info("{}分页请求结束", index);
						break;
					}
				}
				if (currentList != null && currentList.size() > 0) {
					getWebDriverListAddQueue(currentList, herfpath, taskQueue, className);
					oldList = currentList;
				} else {
					LOG.error("从{}根据xpth{}未获取到文章列表", index, herfpath);
				}
				element.click();
			} catch (Exception e) {
				if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
					LOG.error("{} connection error ", index);
				} else {
					LOG.error("{} url unknow error {}", index, e.getMessage());
				}
			}
		} while (element != null);
	}

	public void saveAttachment(OfficialDocumentList officialDocumentList) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setUrl(officialDocumentList.getHref());
		attachment.setTitle(officialDocumentList.getName());
		attachment.setSource(officialDocumentList.getSource());
		attachment.setSecTitle(officialDocumentList.getSecTitle());
		attachment.setInsertDate(officialDocumentList.getInsertDate());
		mongoTemplatet.insert(attachment);
	}

	public TextPage getTextPage(ThreadLocal<WebClient> webClient, WebRequest webRequest, Boolean needProxy) {
		try {
			if (webClient.get() == null) {
				WebClient webClient1 = getWebclient(needProxy);
				webClient.set(webClient1);
			}
			LOG.info("start spider url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			if (!(o instanceof TextPage) || o.getWebResponse().getStatusCode() == 403
					|| o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} not a Text PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else if (o instanceof TextPage) {
				o = ChangePageCharSet(o);
				return (TextPage) o;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", webRequest.getUrl());
			} else {
				LOG.error("{} url unknow error {}", webRequest.getUrl(), e.getMessage());
			}
		} finally {
			if (!needProxy) {
				webClient.remove();
			}
		}
		return null;
	}

	public void getJosnPageListWhile(String index, String pageHref, ThreadLocal<WebClient> webClient, String listName,
			Boolean needProxy, String taskQueue, String className, int startNum) {
		String url = "";
		WebRequest request = null;
		HtmlPage oldHtmlPage = null;
		int i = startNum;
		do {
			try {
				if (i == startNum) {
					url = index;
				} else {
					url = String.format(pageHref, i);
				}
				request = getWebRequest(url);
				HtmlPage htmlPage = getHtmlPageCount(webClient, request, needProxy);
				if (htmlPage == null && oldHtmlPage == null) {
					LOG.info("{}分页请求结束", url);
					break;
				} else if (htmlPage != null && oldHtmlPage != null) {
					if (htmlPage.asText().replace(i + "", "").replace(i - 1 + "", "")
							.equals(oldHtmlPage.asText().replace(i + "", "").replace(i - 1 + "", ""))) {
						LOG.info("{}分页请求结束", url);
						break;
					}
				}
				i++;
				oldHtmlPage = htmlPage;
				if (htmlPage == null) {
					LOG.error("请求{}未获取到内容", url);
				} else {
					parseJsonPageList(htmlPage, listName, className, taskQueue);
				}
			} catch (Exception e) {
				LOG.error("{} getPageListAddQueue while error {}", url, e.getMessage());
			}
		} while (true);
	}

	/***
	 * 由于json格式千差万别， 该方法一般情况下需要重写。
	 * 
	 * @param htmlPage
	 * @param listName
	 * @param className
	 * @param taskQueue
	 */
	void parseJsonPageList(HtmlPage htmlPage, String listName, String className, String taskQueue) {
		String json = htmlPage.getBody().asText();
		JSONObject jsonObject = JSON.parseObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray(listName);
		jsonArray.forEach(e -> {
			JSONObject o = (JSONObject) e;
			String title = o.get("name") != null ? o.get("name").toString() : null;
			String href = o.get("href") != null ? o.get("href").toString() : null;
			if (StringUtils.isNotBlank(href)) {
				OfficialDocumentList officialDocumentList = new OfficialDocumentList();
				officialDocumentList.setHref(href);
				officialDocumentList.setTaskClass(className);
				officialDocumentList.setCrawlerCount(0);
				if (title != null) {
					officialDocumentList.setName(title);
				}
				addQueue(taskQueue, officialDocumentList);
			}
		});

	}

	// 根据图片网络地址下载图片
	public String download(String url, String path) {
		File dirFile = null;
		byte[] size = new byte[1024];

		try {
			path = downPath.concat(path);
			String suffiname = url.substring(url.lastIndexOf("."), url.length());
			String filename = UUID.randomUUID().toString();
			StringBuilder sb = new StringBuilder();
			dirFile = new File(path);
			String fileName = path + sb.append(filename).append(suffiname);
			if (!dirFile.exists()) {
				if (dirFile.mkdir()) {
					if (path.length() > 0) {
					}
				}
			}

			OkHttpClient client = new OkHttpClient();

			Request request = new Request.Builder().url(url).build();

			okhttp3.Call call = client.newCall(request);
			// 请求加入调度
			call.enqueue(new okhttp3.Callback() {
				@Override
				public void onFailure(okhttp3.Call call, final IOException e) {

				}

				@Override
				public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
					InputStream in = response.body().byteStream();
					File file = new File(fileName);
					FileOutputStream fos = new FileOutputStream(file);
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					FileChannel channel = fos.getChannel();
					int num = 0;
					while ((num = in.read(size)) != -1) {
						for (int i = 0; i < num; i++) {
							buffer.put(size[i]);
							buffer.flip(); // 此处必须要调用buffer的flip方法
							channel.write(buffer);
							buffer.clear();
						}
					}
					channel.close();
					fos.close();
				}
			});

			return fileName;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

	public OfficialDocument addAttachments(OfficialDocument officialDocument, HtmlPage htmlPage, String herfpath,
			String basePath) {
		if (htmlPage == null || officialDocument == null || StringUtils.isEmpty(herfpath)) {
			return officialDocument;
		}
		List<DomNode> attachment = htmlPage.getByXPath(herfpath);
		if (attachment != null && !attachment.isEmpty()) {
			List<Attachment> attachments = new ArrayList<>();
			attachment.forEach(e -> {
				HtmlAnchor domAttr = (HtmlAnchor) e;
				String href = domAttr.getHrefAttribute();
				String name = domAttr.getTextContent();
				try {
					if (Util.isFile(href)) {
						Attachment atta = new Attachment();
						href = htmlPage.getFullyQualifiedUrl(href).toString();
						atta.setUrl(href);
						atta.setTitle(name);
						String filePath = download(href, basePath);
						atta.setMyUrl(filePath);
						attachments.add(atta);
					} else {
						LOG.warn("{} unknown attachment file ", officialDocument.getUrl());
					}
				} catch (MalformedURLException e1) {
					LOG.error("real href get error {}", href);
				}

			});
			if (!CollectionUtils.isEmpty(attachments)) {
				officialDocument.setAttachments(attachments);
			}
		}
		return officialDocument;
	}

	public OfficialDocument addImgs(OfficialDocument officialDocument, HtmlPage htmlPage, String herfpath,
			String basePath) {
		List<Img> imgs = new LinkedList<>();
		List<DomElement> imgList = htmlPage.getByXPath(herfpath);
		try {
			for (DomElement dom : imgList) {
				Img img = new Img();
				String imgHref = dom.getAttribute("src");
				imgHref = htmlPage.getFullyQualifiedUrl(imgHref).toString();
				String imgNew = download(imgHref, basePath);
				dom.setAttribute("src", imgNew);
				img.setCurrent_path(imgNew);
				img.setOrig_path(imgHref);
				imgs.add(img);
			}
		} catch (Exception e) {
			LOG.warn("获取地址异常", e.getMessage());
		}
		if (!CollectionUtils.isEmpty(imgs)) {
			officialDocument.setImgs(imgs);
		}
		return officialDocument;
	}
}
