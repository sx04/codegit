package com.cetcbigdata.varanus.core.component;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.utils.SSLSocketClient;
import com.gargoylesoftware.htmlunit.html.*;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.utils.CharacterPinYinConvertUtil;
import com.cetcbigdata.varanus.utils.Constant;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

import static java.util.stream.Collectors.toList;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:15:49
 */
public class PageParserHelper {

	private static final Logger LOG = LoggerFactory.getLogger(PageParserHelper.class);

	public static WebRequest getWebRequest(String url, HttpMethod httpMethod) throws MalformedURLException {
		return new WebRequest(new URL(url), httpMethod);
	}

	public static String assembleQueue(Integer taskId, Integer listId) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.VARANUS_TASK).append(taskId).append(":").append(listId);
		return sb.toString();
	}

	public static String disassembleQueue(String queue) {
		String taskQueue = queue.replace(Constants.VARANUS_TASK, "");
		return taskQueue;
	}

	public static String assebleBloomFilter(String department, String section) {
		StringBuilder sb = new StringBuilder();
		String depart = CharacterPinYinConvertUtil.getPingYin(department);
		String sect = CharacterPinYinConvertUtil.getPingYin(section);
		sb.append(Constants.VARANUS_BLOOM_KEY).append(depart).append(":").append(sect);
		return sb.toString();
	}

	public static String assemblelastCrawlerUrl(Integer taskId, Integer listId) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.VARANUS_LAST_URL).append(taskId).append(":").append(listId);
		return sb.toString();
	}

	/**
	 * @param webClient
	 * @param webRequest
	 * @param needProxy
	 * @return 获取xml返回的页面对象
	 */
	public static XmlPage getXmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest, Boolean needProxy) {
		try {
			LOG.info("获取xml返回的页面对象 url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			if (o instanceof XmlPage) {
				o = ChangePageCharSet(o);
				return (XmlPage) o;
			} else {
				LOG.warn("{} 不是 XML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} 连接错误 ", webRequest.getUrl());
			} else {
				LOG.error("{} url 未知异常 {}", webRequest.getUrl(), e);
			}
		}
		return null;
	}

	/**
	 * okhttp json返回的即为文档详情的信息，只需要解析文档信息即可完成
	 *
	 * @throws Exception
	 */
	public static void parseJsonList(OkHttpClient okHttpClient, JSONObject jsonField, ConfigListEntity configListEntity,
									 List<TaskInfoEntity> taskInfoEntities,String listTemplateUrl) throws Exception {
		String jsonKey = configListEntity.getListJsonKey();
		String jsonSubstring = configListEntity.getListJsonSubstring();
		String listIdUrl = configListEntity.getListIdUrl();
		String idKey = configListEntity.getJsonIdKey();
		int i =1;
		String url = listTemplateUrl.replace("%s", String.valueOf(i));
        Request request = null;
        if (configListEntity.getListRequestType().equals(HttpMethod.GET.name())) {
            request = new Request.Builder().url(url).build();
        } else if (configListEntity.getListRequestType().equals(HttpMethod.POST.name())) {
            FormBody.Builder builder = new FormBody.Builder();
            if (!StringUtils.isBlank(configListEntity.getListRequestParams())){
                Map<String, String> bodys = (Map) JSON.parse(configListEntity.getListRequestParams());
                for(String key: bodys.keySet()){
                    builder.add(key,bodys.get(key));
                }
                if(!StringUtils.isBlank(configListEntity.getListPageName())){
                    builder.add(configListEntity.getListPageName(),String.valueOf(i));
                }
            }
            RequestBody formBody = builder.build();
            request = new Request.Builder().url(url).post(formBody).build();
        }
        //增加浏览器头部信息
        if (!StringUtils.isBlank(configListEntity.getListPageHeader())){
            Map<String, String> maps = (Map) JSON.parse(configListEntity.getListPageHeader());
            for(String key: maps.keySet()){
                request = request.newBuilder().addHeader(key,maps.get(key)).build();
            }
        }
		Response response = null;
		response = okHttpClient.newCall(request).execute();
		if (response.isSuccessful()) {
			byte[] res = response.body().bytes();
			String json = new String(res);
			if (!StringUtils.isBlank(jsonSubstring)) {
				String[] jsonSubstrings = jsonSubstring.split(",");
				json = json.substring(json.indexOf(jsonSubstrings[0]) + 1, json.lastIndexOf(jsonSubstrings[1]));
			}
			// LOG.info("开始抓取url: {}", url);
			JSONObject jsonObject = JSON.parseObject(json);
			JSONArray jsonArray = null;
			if (!StringUtils.isBlank(jsonKey)) {
				String[] jsonKeys = jsonKey.split(",");
				for (int j = 0; j < jsonKeys.length; j++) {
					if (j == jsonKeys.length - 1) {
						jsonArray = jsonObject.getJSONArray(jsonKeys[j]);
					} else {
						jsonObject = jsonObject.getJSONObject(jsonKeys[j]);
					}
				}
			}
			for (Object e : jsonArray) {
				TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
				JSONObject o = (JSONObject) e;
				if(jsonField != null) {
					for (String key : jsonField.keySet()) {
						if (o.containsKey(key)) {
							String value = o.getString(key);
							if (!StringUtils.isBlank(value)) {
								LOG.info("value: {}", value);
								Field field = taskInfoEntity.getClass().getDeclaredField((String) jsonField.get(key));
								field.setAccessible(true);
								field.set(taskInfoEntity, value);
							}
						}
					}
				}
				if (idKey != null && o.containsKey(idKey)) {
					String id = o.getString(idKey);
					String docUrl = listIdUrl;
					if(id.contains("../")){
						id = id.replace("../","");
						String[] chars = docUrl.split("/");
						docUrl = docUrl.replace("/" + chars[chars.length-2],"");
					}
					if(id.contains("./")){
						id = id.replace("./","");
					}
					taskInfoEntity.setSectionUrl(docUrl.replace("%s", id));
				}
				taskInfoEntities.add(taskInfoEntity);
			}
		}
	}


	/**
	 * okhttp json返回的即为文档详情的信息，只需要解析文档信息即可完成
	 *
	 * @throws Exception
	 */
	public static void parseJsonList(ThreadLocal<WebClient> webClient, JSONObject jsonField, ConfigListEntity configListEntity,
									 List<TaskInfoEntity> taskInfoEntities,String listTemplateUrl) throws Exception {
		String jsonKey = configListEntity.getListJsonKey();
		String jsonSubstring = configListEntity.getListJsonSubstring();
		String listIdUrl = configListEntity.getListIdUrl();
		String idKey = configListEntity.getJsonIdKey();
		int i =1;
		String url = listTemplateUrl.replace("%s", String.valueOf(i));
		WebRequest webRequest = null;
		if (configListEntity.getListRequestType().equals(HttpMethod.GET.name())) {
			webRequest = PageParserHelper.getWebRequest(url, HttpMethod.GET);
		} else if (configListEntity.getListRequestType().equals(HttpMethod.POST.name())) {
			if (!StringUtils.isBlank(configListEntity.getListRequestParams())) {
				webRequest = PageParserHelper.getWebRequest(url, HttpMethod.POST);
				PageParserHelper.jsonPairsToMap(configListEntity.getListRequestParams(), webRequest);
			}
		}
		//增加浏览器头部信息
		if (!StringUtils.isBlank(configListEntity.getListPageHeader())){
			PageParserHelper.jsonHeaders(configListEntity.getListPageHeader(),webRequest);
		}
		HtmlPage page = null;
		page = PageParserHelper.getHtmlPage(webClient, webRequest, false);
		if (Optional.ofNullable(page).isPresent() &&page.getWebResponse().getStatusCode()==200) {
			String json = page.getWebResponse().getContentAsString();
			if(Util.isXmlDocument(json)){
				json = Util.convertXmlToJson(json);
			}
			if (!StringUtils.isBlank(jsonSubstring)) {
				String[] jsonSubstrings = jsonSubstring.split(",");
				json = json.substring(json.indexOf(jsonSubstrings[0]) + 1, json.lastIndexOf(jsonSubstrings[1]));
			}
			// LOG.info("开始抓取url: {}", url);
			JSONArray jsonArray = null;
			try {
				JSONObject jsonObject = JSON.parseObject(json);

				if (!StringUtils.isBlank(jsonKey)) {
					String[] jsonKeys = jsonKey.split(",");
					for (int j = 0; j < jsonKeys.length; j++) {
						if (j == jsonKeys.length - 1) {
							jsonArray = jsonObject.getJSONArray(jsonKeys[j]);
						} else {
							jsonObject = jsonObject.getJSONObject(jsonKeys[j]);
						}
					}
				}
			}catch (Exception e){
				if (e instanceof com.alibaba.fastjson.JSONException){
					Object o =  JSON.parse(json);
					if (o instanceof JSONArray){
						jsonArray = (JSONArray) o;
					}else {
						throw e;
					}
				}else {
					throw e;
				}
				LOG.error("解析json出错");
			}
			for (Object e : jsonArray) {
				TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
				JSONObject o = (JSONObject) e;
				if(jsonField != null) {
					for (String key : jsonField.keySet()) {
						if (o.containsKey(key)) {
							String value = o.getString(key);
							if (!StringUtils.isBlank(value)) {
								Field field = taskInfoEntity.getClass().getDeclaredField((String) jsonField.get(key));
								field.setAccessible(true);
								field.set(taskInfoEntity, value);
							}
						}
					}
				}
				if (idKey != null && o.containsKey(idKey)) {
					String id = o.getString(idKey);
					String docUrl = listIdUrl;
					if(id.contains("./")){
						taskInfoEntity.setSectionUrl(getUrl(id,page));
					}else {
						taskInfoEntity.setSectionUrl(docUrl.replace("%s", id));
					}
				}
				taskInfoEntities.add(taskInfoEntity);
			}
		}
	}



	/**
	 * webclient json返回的即为文档详情的信息，只需要解析文档信息即可完成
	 *
	 * @throws Exception
	 */
	public static void parseJsonListWebclient(ThreadLocal<WebClient> webClient, WebRequest webRequest, JSONObject jsonField, ListDetail docList,
									 List<OfficialDocument> officialDocuments) throws Exception {
		String jsonKey = docList.getListJsonKey();
		String jsonSubstring = docList.getListJsonSubstring();
		String listIdUrl = docList.getListIdUrl();
		String idKey = docList.getJsonIdKey();
		Integer listPageNumber = docList.getListPageNumber();
		String pageTemplate = docList.getListTemplateUrl();
		int i =1;
		String url = pageTemplate.replace("%s", String.valueOf(i));
		//WebRequest webRequest = new WebRequest(new URL(url));
		WebResponse webResponse = webClient.get().loadWebResponse(webRequest);
		Charset charset = webResponse.getContentCharsetOrNull();
		charset = Charset.defaultCharset();
//		if (charset != null && charset.equals(Charset.forName("gb2312"))) {
//			charset = Charset.forName("GBK");
//		}
		String json = webResponse.getContentAsString(charset);
		if (!StringUtils.isBlank(jsonSubstring)) {
			String[] jsonSubstrings = jsonSubstring.split(",");
			json = json.substring(json.indexOf(jsonSubstrings[0]) + 1, json.lastIndexOf(jsonSubstrings[1]));
		}
		// LOG.info("开始抓取url: {}", url);
		JSONObject jsonObject = JSON.parseObject(json);
		JSONArray jsonArray = null;
		if (!StringUtils.isBlank(jsonKey)) {
			String[] jsonKeys = jsonKey.split(",");
			for (int j = 0; j < jsonKeys.length; j++) {
				if (j == jsonKeys.length - 1) {
					jsonArray = jsonObject.getJSONArray(jsonKeys[j]);
				} else {
					jsonObject = jsonObject.getJSONObject(jsonKeys[j]);
				}
			}
		}
		for (Object e : jsonArray) {
			OfficialDocument officialDocument = new OfficialDocument();
			JSONObject o = (JSONObject) e;
			if(jsonField != null) {
				for (String key : jsonField.keySet()) {
					if (o.containsKey(key)) {
						String value = o.getString(key);
						if (!StringUtils.isBlank(value)) {
							LOG.info("value: {}", value);
							Field field = officialDocument.getClass().getDeclaredField((String) jsonField.get(key));
							field.setAccessible(true);
							field.set(officialDocument, value);
						}
					}
				}
			}
			if (idKey != null && o.containsKey(idKey)) {
				String id = o.getString(idKey);
				String docUrl = listIdUrl;
				if(id.contains("../")){
					id = id.replace("../","");
					String[] chars = docUrl.split("/");
					docUrl = docUrl.replace("/" + chars[chars.length-2],"");
				}
				if(id.contains("./")){
					id = id.replace("./","");
				}
				officialDocument.setUrl(docUrl.replace("%s", id));
			}
			officialDocuments.add(officialDocument);
		}

	}

	/**
	 *
	 * @param webClient
	 * @param webRequest
	 * @param needProxy
	 * @return 主要通过webRequest来获得一个HtmlPage
	 */
	public static HtmlPage getHtmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest, Boolean needProxy) {
		try {
			LOG.info("获得一个HtmlPage url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage() || o.getWebResponse().getStatusCode() == 403
					|| o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} 不是 HTML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else {
				o = ChangePageCharSet(o);
				HtmlPage htmlPage = (HtmlPage) o;
				return htmlPage;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} 连接错误 ", webRequest.getUrl());
			} else {
				LOG.error("{} url 未知异常 {}", webRequest.getUrl(), e);
			}
		} finally {
//            if (!needProxy) {
//                webClient.remove();
//            }
		}
		return null;
	}


	/**
	 *
	 * @param webClient
	 * @param webRequest
	 * @param needProxy
	 * @return 主要通过webRequest来获得一个HtmlPage
	 */
	public static HtmlPage getHtmlPage(WebClient webClient, WebRequest webRequest, Boolean needProxy) throws IOException {
		try {
			Page o = webClient.getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage() || o.getWebResponse().getStatusCode() == 403
					|| o.getWebResponse().getStatusCode() == 500
					|| o.getWebResponse().getStatusCode() == 401) {
				LOG.warn("{} 不是 HTML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else {
				o = ChangePageCharSet(o);
				HtmlPage htmlPage = (HtmlPage) o;
				return htmlPage;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} 连接错误 ", webRequest.getUrl());
			} else {
				LOG.error("{} url 未知异常 {}", webRequest.getUrl(), e);
			}
			throw  e;
		}
	}


	/**
	 *
	 * @param needProxy
	 * @return 主要通过webRequest来获得一个HtmlPage
	 */
	public static WebDriver getWebDriverPage(ThreadLocal<WebDriver> webDriverThreadLocal, String url,
			Boolean needProxy) {
		try {
			LOG.info("start spider url: {}", webDriverThreadLocal.get().getCurrentUrl());
			webDriverThreadLocal.get().get(url);
			return webDriverThreadLocal.get();
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", url);
			} else {
				LOG.error("{} url unknow error {}", url, e.getMessage());
			}
			return null;
		} finally {
		}
	}

	/**
	 *
	 * @param webClient
	 * @param webRequest
	 * @return 主要通过webRequest来获得一个HtmlPage
	 */
	public static Object getHtmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest) {
		try {
			LOG.info("获得一个HtmlPage url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage() && !(o.getWebResponse().getStatusCode() == 403)
					&& !(o.getWebResponse().getStatusCode() == 500)) {
				// 判断链接是否是附件
				return o;
			} else if (o.getWebResponse().getStatusCode() == 403 || o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} 不是一个 HTML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else {
				o = ChangePageCharSet(o);
				HtmlPage htmlPage = (HtmlPage) o;
				return htmlPage;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} 连接异常 ", webRequest.getUrl());
			} else {
				LOG.error("{} url 未知异常 {}", webRequest.getUrl(), e.getMessage());
			}
		} finally {
//            if (!needProxy) {
//                webClient.remove();
//            }
		}
		return null;
	}

	/**
	 *
	 * @param webClient
	 * @param webRequest
	 * @return 监测网站页面是否正常
	 */
	public static HtmlPage checkHtmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest) {
		try {
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
		}
		return null;
	}

	/**
	 *
	 * @param webClient
	 * @param webRequest
	 * @return 监测网站页面是否正常
	 */
	public static XmlPage checkXmlPage(ThreadLocal<WebClient> webClient, WebRequest webRequest) {
		try {
			LOG.info("start spider url: {}", webRequest.getUrl());
			Page o = webClient.get().getPage(webRequest);
			o = changeUnexpectedPage(o);
			if (!o.isHtmlPage() || o.getWebResponse().getStatusCode() == 403
					|| o.getWebResponse().getStatusCode() == 500) {
				LOG.info("{} not a HTML PAGE {}", webRequest.getUrl(), o.getWebResponse());
				return null;
			} else {
				o = ChangePageCharSet(o);
				XmlPage xmlPage = (XmlPage) o;
				return xmlPage;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketException || e instanceof IOException) {
				LOG.error("{} connection error ", webRequest.getUrl());
			} else {
				LOG.error("{} url unknow error {}", webRequest.getUrl(), e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 检测异常页面
	 * 
	 * @param o
	 * @return
	 */
	public static Page changeUnexpectedPage(Page o) {
		if (o != null || o instanceof UnexpectedPage
				&& ("application/json".equals(o.getWebResponse().getContentType()) || "text/plain".equals(o.getWebResponse().getContentType())) ) {
			try {
				o = HTMLParser.parseHtml(o.getWebResponse(), o.getEnclosingWindow());
			} catch (IOException e) {
				LOG.error("{} 请求网页UnexpectedPage {}", o.getUrl(), e.getMessage());
			}
		}
		return o;

	}

	/**
	 * 改变字符编码
	 * 
	 * @param page
	 * @return
	 */
	public static Page ChangePageCharSet(Page page) {
		WebResponse webResponse = page.getWebResponse();
		StringWebResponse response = null;
		InputStream is =null;
		Charset charset = webResponse.getContentCharset();
		if (charset != null && (charset.equals(Charset.forName("gb2312")))) {
			charset = Charset.forName("GBK");
		}
		else if (charset != null && charset.equals(Charset.forName("ISO-8859-1"))){
			charset = Charset.forName("UTF-8");
		}
		else {
			return page;
		}
		try {
			is = webResponse.getContentAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
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
			if (is!=null){
				try {
					is.close();
				} catch (IOException e) {
					LOG.error("关闭流失败",e);
				}
			}
		}
		return page;

	}

	public static Elements getElements(Document doc, String tagName) {
		if (doc == null) {
			return null;
		}
		Elements elements = doc.getElementsByTag(tagName);
		return elements;
	}

	public static List<DomNode> getDomNodeList(HtmlPage htmlPage, String herfpath) {
		if (htmlPage == null || StringUtils.isEmpty(herfpath)) {
			return null;
		}
		List<DomNode> domNodes = htmlPage.getByXPath(herfpath);
		return domNodes;
	}

	public static List<DomNode> getDomNodeList(XmlPage xmlPage, String herfpath) {
		if (xmlPage == null || StringUtils.isEmpty(herfpath)) {
			return null;
		}
		List<DomNode> domNodes = xmlPage.getByXPath(herfpath);
		return domNodes;
	}

	/**
	 *
	 *
	 * @param url
	 * @param i
	 * @return 获取格式化的url地址
	 */
	public static String getFormatUrl(String url, int i) {
		String uri = url;
		try {
			uri = String.format(url, i);
		}catch (Exception e){
			LOG.error("格式化url异常 {}",url);
			uri = uri.replace("%s",String.valueOf(i));
		}
		return uri;
	}

	public static String getFormatUrl(String url, String year, int i) {
		return String.format(url, year, i);
	}

	/**
	 * @param html
	 * @return 将字符串转换为网页Document
	 */
	public static Document getDocument(String html) {
		return Jsoup.parse(html);
	}

	/**
	 * @param href
	 * @param htmlPage
	 * @return 通过webclient获取url在页面的绝对路径
	 */
	public static  String getUrl(String href, HtmlPage htmlPage) {
		try {
			String hrefNew =  "";
			hrefNew = htmlPage.getFullyQualifiedUrl(href).toString();
			return hrefNew;
		} catch (MalformedURLException e1) {
			LOG.error("获取页面绝对路径错误 {}", href);
		}
		return null;
	}


	/**
	 *
	 *
	 * @param e
	 * @param baseUrl
	 * @return 传递一个baseUrl："http://www.sass.cn",在元素e中设置后获取href，再根据host判断， sameHost
	 */
	public static String getAbsUrl(Element e, String baseUrl) {
		e.setBaseUri(baseUrl);
		String href = e.absUrl("href");
		try {
			URL url = new URL(href);
			URL base = new URL(baseUrl);
			if (!base.getHost().equals(url.getHost())) {
				href = null;
			}
		} catch (MalformedURLException e1) {
			LOG.error("abs href get error {}", href);
		}
		return href;
	}

	public static void jsonPairsToMap(String json, WebRequest webRequest) {
		Map maps = (Map) JSON.parse(json);
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		for (Object map : maps.entrySet()) {
			nameValuePairs.add(new NameValuePair(((Map.Entry) map).getKey().toString(), ((Map.Entry) map).getValue().toString()));
		}
		webRequest.setRequestParameters(nameValuePairs);
	}

	public static void jsonHeaders(String json, WebRequest webRequest) {
		Map<String, String> maps = (Map) JSON.parse(json);
		webRequest.setAdditionalHeaders(maps);
	}

	// 根据图片网络地址下载图片
	public  static String download(String url,Img img,String path,OkHttpClient client) {
		byte[] size = new byte[2048];
		StringBuilder sb1 = new StringBuilder(url);
		String suffiname = sb1.substring(sb1.lastIndexOf("."), sb1.length());
		String filename = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder(path);
		File dirFile = new File(path);
		String fileName = sb.append(filename).append(suffiname).toString();
		try {
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		} catch (Exception e) {
			LOG.error("创建文件夹失败", e);
		}
		String downloadUrl = new String(url);
		LOG.info("开始执行下载附件-> {}", downloadUrl);
		Response response = null;
		FileChannel channel = null;
		FileOutputStream fos = null;
		try {
			Request request = new Request.Builder().url(downloadUrl).build();
            if (downloadUrl.contains("https")) {
                response = client.newBuilder().readTimeout
                        (30, TimeUnit.SECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                        .build()
                        .newCall(request)
                        .execute();
            }
            else {
                response = client.newCall(request).execute();
            }
			if (response.isSuccessful()) {
				InputStream in = response.body().byteStream();
				File file = new File(fileName);
				fos = new FileOutputStream(file);
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				channel = fos.getChannel();
				int num = 0;
				while ((num = in.read(size)) != -1) {
					for (int i = 0; i < num; i++) {
						buffer.put(size[i]);
						buffer.flip(); // 此处必须要调用buffer的flip方法
						channel.write(buffer);
						buffer.clear();
					}
				}


				img.setImgsStatus(1);
			} else {
				img.setImgsStatus(2);
			}
			return fileName;

		}catch (Exception e) {
			LOG.error("下载文件失败 {},{}", e.getMessage(), downloadUrl);
			img.setImgsStatus(2);
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	// 根据图片网络地址下载附件
	public   static String download(String url,Attachment attachment,String path,OkHttpClient client) {
		byte[] size = new byte[2048];
		StringBuilder sb1 = new StringBuilder(url);
		String suffiname = sb1.substring(sb1.lastIndexOf("."), sb1.length());
		String filename = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder(path);
		File dirFile = new File(path);
		String fileName = sb.append(filename).append(suffiname).toString();
		try {
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		} catch (Exception e) {
			LOG.error("创建文件夹失败", e);
		}
		String downloadUrl = new String(url);
		LOG.info("开始执行下载附件-> {}", downloadUrl);
		FileChannel channel = null;
		FileOutputStream fos = null;
		Response response = null;
		InputStream in = null;
		try {
			Request request = new Request.Builder().url(downloadUrl).build();
            if (downloadUrl.contains("https")){
                response = client.newBuilder().readTimeout
                        (20, TimeUnit.SECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                        .build()
                        .newCall(request)
                        .execute();
            }
            else{
                response = client.newCall(request).execute();
            }
			if (response.isSuccessful()) {

				in = response.body().byteStream();
				File file = new File(fileName);
				fos = new FileOutputStream(file);
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				channel = fos.getChannel();
				int num = 0;
				while ((num = in.read(size)) != -1) {
					for (int i = 0; i < num; i++) {
						buffer.put(size[i]);
						buffer.flip(); // 此处必须要调用buffer的flip方法
						channel.write(buffer);
						buffer.clear();
					}
				}
				attachment.setAttachmentsStatus(1);
			} else {
				attachment.setAttachmentsStatus(2);
			}
			return fileName;

		} catch (Exception e) {
			LOG.error("下载文件失败 {},{}", e.getMessage(), downloadUrl);
			attachment.setAttachmentsStatus(2);
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in !=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static  void addAttachmentsHtml(HtmlPage htmlPage,String xpathValue, String xpathKey, String basePath,Map detailData, OkHttpClient okHttpClient) {
		List<DomElement> attachment = htmlPage.getByXPath(xpathValue);
		if (CollectionUtils.isNotEmpty(attachment)) {
			List<Attachment> attachments = new ArrayList<>();
			for(DomElement e:attachment) {
				try {
					String href = e.getAttribute("href");
					LOG.info("附件的原始路径是->{}", href);
					String name = e.getTextContent();
					String hrefNew = getUrl(href, htmlPage);
					if (Util.isFile(hrefNew, okHttpClient)) {
						Attachment atta = new Attachment();
						atta.setAttachmentsStatus(0);
						LOG.info("获取到的绝对路径是->{}", hrefNew);
						atta.setUrl(hrefNew);
						atta.setTitle(name);
						String filePath = download(hrefNew, atta, basePath, okHttpClient);
						if (atta.getMyUrl() != null) {
							atta.setMyUrl(hrefNew);
						} else {
							atta.setMyUrl(filePath);
							if (!StringUtils.isEmpty(filePath)) {
								//String localUrl = filePath.replace(basePath,localAttachmentsUrl);
								e.setAttribute("href", filePath);
							}
						}
						attachments.add(atta);
					} else {
						LOG.warn("{} unknown attachment file ", hrefNew);
					}
				}catch (Exception ee){
					LOG.warn("下载附件失败 {}",htmlPage.getUrl(),ee);
				}
			}
			if (!CollectionUtils.isEmpty(attachments)) {
				detailData.put(xpathKey,JSON.toJSONString(attachments));
			}
		}
	}

	public  static void addAttachmentsWebDriver(OfficialDocument officialDocument, WebDriver webDriver,
			String herfpath, String basePath,String localAttachmentsUrl,OkHttpClient okHttpClient) {
		List<WebElement> attachment = webDriver.findElements(By.xpath(herfpath));
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		if (CollectionUtils.isNotEmpty(attachment)) {
			List<Attachment> attachments = new ArrayList<>();
			for (WebElement e : attachment) {
				try {
					String href = e.getAttribute("href");
					String name = e.getText();
					if (Util.isFile(href, okHttpClient)) {
						Attachment atta = new Attachment();
						atta.setUrl(href);
						atta.setTitle(name);
						atta.setAttachmentsStatus(0);
						String filePath = download(href, atta, basePath, okHttpClient);
						if (!StringUtils.isEmpty(filePath)) {
							//String localUrl = filePath.replace(basePath,localAttachmentsUrl);
							setAttribute(e, "href", filePath, js);
						}
						atta.setMyUrl(filePath);
						attachments.add(atta);
					} else {
						LOG.warn("{} not attachment", officialDocument.getUrl());
					}
				} catch (Exception ee) {
					LOG.warn("下载附件失败 {}", webDriver.getCurrentUrl(), ee);
				}
			}
			if (!CollectionUtils.isEmpty(attachments)) {
				officialDocument.setAttachments(JSON.toJSONString(attachments));
			}
		}
	}


	public static void addImgs(Map detailData, HtmlPage htmlPage,String xpathValue, String xpathKey, String basePath,String localImgsUrl,OkHttpClient client) {
		List<Img> imgs = new LinkedList<>();
		List<DomElement> imgList = htmlPage.getByXPath(xpathValue);
		for (DomElement dom : imgList) {
			try {
				Img img = new Img();
				img.setImgsStatus(0);
				String imgHref = dom.getAttribute("src");
				imgHref = getUrl(imgHref, htmlPage);
				if (!StringUtils.isBlank(imgHref)) {
					String imgNew = download(imgHref, img, basePath, client);
					if (!StringUtils.isBlank(imgNew)) {
						String localPath = imgNew.replace(basePath, localImgsUrl);
						dom.setAttribute("localPath", localPath);
					} else {
						dom.setAttribute("localPath", "");
					}
					dom.setAttribute("src", imgNew);

					dom.setAttribute("originSrc", imgHref);
					img.setCurrentPath(imgNew);
					img.setOriginPath(imgHref);
					imgs.add(img);
				}
			} catch (Exception ee) {
				LOG.warn("下载附件失败 {}", htmlPage.getUrl(), ee);
			}
		}
		if (!CollectionUtils.isEmpty(imgs)) {
			detailData.put(xpathKey,JSON.toJSONString(imgs));

		}
	}

	public static void addImgs(OfficialDocument officialDocument, WebDriver webDriver, String imgPath,
			String basePath,String localImgsUrl,OkHttpClient client) {
		List<Img> imgs = new LinkedList<>();
		List<WebElement> imgList = webDriver.findElements(By.xpath(imgPath));
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		for (WebElement dom : imgList) {
			Img img = new Img();
			img.setImgsStatus(0);
			String imgHref = dom.getAttribute("src");
			if (!StringUtils.isBlank(imgHref)) {
				String imgNew = download(imgHref,img, basePath,client);
				if (StringUtils.isNoneBlank(imgNew)) {
					String localPath = imgNew.replace(basePath, localImgsUrl);
					setAttribute(dom, "src", imgNew, js);
					setAttribute(dom, "localPath", localPath, js);
					setAttribute(dom, "originSrc", imgHref, js);
					img.setCurrentPath(imgNew);
					img.setOriginPath(imgHref);
					imgs.add(img);
				}
			}
		}
		if (!CollectionUtils.isEmpty(imgs)) {
			officialDocument.setImgs(JSON.toJSONString(imgs));
		}
	}

	public static void setAttribute(WebElement element, String attName, String attValu, JavascriptExecutor driver) {
		driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attName, attValu);
	}

	/**
	 * 检测信息盒字段如publishOffice和source，如果有冒号分割则返回内容部分
	 *
	 * @return String型整数
	 */
	public static String checkInfo(String info) {
		String infoValue = trimText(info);
		if (StringUtils.isBlank(infoValue)) {
			return "";
		} else if (infoValue.contains(":")) {
			String[] infoList = infoValue.split(":");
			if (infoList.length == 2) {
				infoValue = trimText(infoList[1]);
				return infoValue;
			}
		} else if (infoValue.contains("：")) {
			String[] infoList = infoValue.split("：");
			if (infoList.length == 2) {
				infoValue = trimText(infoList[1]);
				return infoValue;
			}
		}
		else if (infoValue.contains("】")) {
			String[] infoList = infoValue.split("】");
			if (infoList.length == 2) {
				infoValue = trimText(infoList[1]);
				return infoValue;
			}
		}
		else {
			return infoValue;
		}
		return "";
	}

	/**
	 * 此方法实现对政策日期标签内容合规判定。
	 *
	 * @return int型整数
	 */
	public static int checkPolicyDate(String lastD, String dateString) {

		lastD = trimText(lastD);
		int checkLastD = -2;

		if (StringUtils.isBlank(lastD) || StringUtils.isBlank(dateString) || !lastD.contains(dateString)) {
			checkLastD = -1;
			return checkLastD;//传入字符串不含有日期
		}

         if(lastD.equals(dateString)) {
			 checkLastD = 0;
			 return checkLastD;//传入字符串只含有日期
		 }
		String[] lastDList = lastD.split(dateString, 2);
		int listLen = lastDList.length;

		// 只允许lastD含有一个dateString
        if (listLen == 1) {
			checkLastD = 1;//政策日期和发布机构不在同一标签
		} else if (listLen == 2) {
			if (trimText(lastDList[1]).length() < 1) {
				checkLastD = 2;//政策日期和发布机构可能在同一标签
			}
		}
		return checkLastD;
	}



	/**
	 * 此方法实现对发布机构标签内容合规判定 。
	 *
	 * @return int型整数
	 */
	public static int checkPubOfficeInfo(String lastO) {

		lastO = trimText(lastO);
		int checkLastO = -7;
		// 不允许出现的标点符号
		String[] errMark = new String[]{ ":", ";", "!", ".", "：", "；", "!", "。", "￥","、","《","》","．" };

        //发布机构名称最后一个字匹配库
		String[] isMark0 =new String[] { "厅", "局", "处", "市", "国", "家", "司", "省", "县", "处", "镇", "科", "部", "会", "室", "组", "队", "中",
				"委", "长", "学", "校", "团", "员", "军", "营", "席", "区", "乡", "村", "州", "院", "所", "士", "连", "排", "级", "班", "师",
				"席", "厂", "理", "教", "派", "宫", "衔", "属", "行", "庙", "寺", "星", "系", "府", "职", "线", "党", "记", "岛", "王", "总",
				"主", "副", "分", "堂", "店", "域", "铺", "界", "间", "社", "殿", "馆", "办", "事", "医", "警", "道", "街", "网", "门",
				"站" ,"心"};
		Set isMark = new HashSet(Arrays.asList(isMark0));
		if ( lastO.length() > 45) {
			return -6;//传入字符串过长
		}
		if(lastO.length() < 3 ) {
			return -5;//传入字符串过短
		}
		if (lastO.contains("发布") || lastO.contains("日期") || lastO.contains("来源") || lastO.contains("附件")
				|| lastO.contains("于") || lastO.contains("至")) {
			checkLastO = -4;
			return checkLastO;//传入字符串包含不合法内容
		}
		for (String mark : errMark) {
			if (lastO.contains(mark)) {
				checkLastO = -3;
				return checkLastO;//传入字符串包含不合法标点符号
			}
		}
		String regNo = "^.*\\d{5,}.*$";
		if (lastO.matches (regNo)) {
			checkLastO = -2;
			return checkLastO;//传入字符串包含不合法数字
		}
		checkLastO = 0;
		if(isMark.contains(lastO.substring(lastO.length()-1))) {
			checkLastO = 1;
			return checkLastO;//发布机构最后一个字匹配成功
		}
		return checkLastO;
	}

	/**
	 * 此方法实现对字符串空格过滤
	 */
	public static String trimText(String lastD) {
		String trim1 = "^[　*| *| *| *|\\s*| *|\uE003*|\r*|\n*]*";// 中英空字符前缀
		String trim2 = "[　*| *| *| *|\\s*| *|\r*|\n*]*$";// 中英空字符后缀
		String trim3 = "[　*| *| *| *|\\s*| *|\r*|\n*]+";// 字符串间中英空字符
		return lastD.replaceAll(trim1, "").replaceAll(trim2, "").replaceAll(trim3, " ");
	}

	/**
	 * 此方法实现对不同类型元素字符串获取
	 */
	public static <T> String getText(T lastD) {
		if (lastD instanceof DomNode) {
			return ((DomNode) lastD).asText();
		} else if (lastD instanceof WebElement) {
			return ((WebElement) lastD).getText();

		} else {
			return "";
		}
	}

	/**
	 * 此方法实现设置政策日期和发布机构字段
	 */
	public static <T> void setDateOfficial(List<T> contentHtml, OfficialDocument officialDocument) {
		if (!CollectionUtils.isEmpty(contentHtml)) {
			int contentNo = -1;
			int contentSize = contentHtml.size();// 获取可能含有政策日期标签的列表
			String lastD = "";

			// 初步判定列表标签中是否可能含有政策日期
			for (int n = 1; contentSize >= n; n++) {
				lastD = trimText(getText(contentHtml.get(contentSize - n)));
				if (!StringUtils.isBlank(lastD) && lastD.length() > 7 && StringUtils.deleteWhitespace(lastD).length() < 55) {
					contentNo = n;
					break;
				}
			}
			if (contentNo > 0) {
				String dateString = Util.getDateStrByRegx(lastD);

				if (!StringUtils.isBlank(dateString) && checkPolicyDate(lastD, dateString) > -1) {
					// 政策日期可能在公文末尾，第一次匹配政策日期
					officialDocument.setPolicyDate(dateString);
					contentNo = contentNo + 1;
					String lastO = contentHtml.size() - contentNo >= 0
							? getText(contentHtml.get(contentHtml.size() - contentNo))
							: "";
					lastO = trimText(lastO);
					if(lastO.contains(dateString)){
						lastO = lastO.replace(dateString,"");
					}
					if (checkPolicyDate(lastD, dateString) == 0 && !StringUtils.isBlank(lastO)
							&& checkPubOfficeInfo(lastO) > 0) {
						// 判断政策日期列表标签只含有政策日期内容，从上一个标签获取发布机关
						if (StringUtils.isBlank(officialDocument.getPubOfficeInfo())) {

							officialDocument.setPubOfficeInfo(lastO);// 写入发布机构
							officialDocument.setCrawlerPolicyOffice(lastO);//写入政策机构
						}
					} else if (checkPolicyDate(lastD, dateString) > 0) {
						// 判断政策日期列表标签含有政策日期以外内容
						lastD = trimText(lastD.split(dateString, 2)[0]);
						if(lastD.contains(dateString)){
							lastD = lastD.replace(dateString,"");
						}
						if (checkPubOfficeInfo(lastD) > 0) {
							// 从同一个标签获取发布机关
							if (StringUtils.isBlank(officialDocument.getPubOfficeInfo())) {
								officialDocument.setPubOfficeInfo(lastD);// 写入发布机构
								officialDocument.setCrawlerPolicyOffice(lastD);//写入政策机构
							}
						} else if (!StringUtils.isBlank(lastO) && checkPubOfficeInfo(lastO) > 0) {
							// 从上一个标签获取发布机关
							if (StringUtils.isBlank(officialDocument.getPubOfficeInfo())) {
								officialDocument.setPubOfficeInfo(lastO);// 写入发布机构
								officialDocument.setCrawlerPolicyOffice(lastO);// 写入政策机构
							}
						}
					}
				} else {
					// 政策日期可能在公文非末尾，第二次获取政策日期
					// 默认在公文中的政策日期的下一个标签为空行
					for (int n = contentNo; contentSize >= n; n++) {
						lastD = trimText(getText(contentHtml.get(contentSize - n)));
						if(lastD.contains(dateString)){
							lastD = lastD.replace(dateString,"");
						}
						if (checkPolicyDate(lastD, Util.getDateStrByRegx(lastD)) == 0) {
							// 标签内容只含有政策日期
							dateString = Util.getDateStrByRegx(lastD);
							contentNo = n + 1;
							officialDocument.setPolicyDate(dateString);// 写入政策日期
							String lastO = contentHtml.size() - contentNo >= 0
									? getText(contentHtml.get(contentHtml.size() - contentNo))
									: "";
							lastO = trimText(lastO);
							if(lastO.contains(dateString)){
								lastO = lastO.replace(dateString,"");
							}
							if (!StringUtils.isBlank(lastO) && checkPubOfficeInfo(lastO) > 0) {
							/*	if (StringUtils.isBlank(officialDocument.getPubOfficeInfo())) {
									officialDocument.setPubOfficeInfo(lastO);// 写入发布机构
								}*/
							officialDocument.setCrawlerPolicyOffice(lastO);
							}
							break;
						} else if (checkPolicyDate(lastD, Util.getDateStrByRegx(lastD)) >= 1) {
							// 同一标签内容含有发布机关和政策日期
							if (checkPubOfficeInfo(lastD.split(Util.getDateStrByRegx(lastD), 2)[0]) > 0) {
								if (checkPolicyDate(lastD, Util.getDateStrByRegx(lastD)) == 1 || (checkPolicyDate(lastD,
										Util.getDateStrByRegx(lastD)) == 2 && (checkPubOfficeInfo(lastD.split(Util.getDateStrByRegx(lastD), 2)[1]) >= 0
										|| checkPubOfficeInfo(lastD.split(Util.getDateStrByRegx(lastD), 2)[1]) == -5))) {
									dateString = Util.getDateStrByRegx(lastD);
									officialDocument.setPolicyDate(dateString);// 写入政策日期
									String lastO = trimText(lastD.split(dateString, 2)[0]);
									if(lastO.contains(dateString)){
										lastO = lastO.replace(dateString,"");
									}
									if (StringUtils.isBlank(officialDocument.getPubOfficeInfo())) {
										officialDocument.setPubOfficeInfo(lastO);// 写入发布机构
										checkPolicyDate(lastD, Util.getDateStrByRegx(lastD));
									}
									break;
								}
							}
						}
					}
				}
			}
		}
		if (!StringUtils.isNotBlank(officialDocument.getPolicyDate())
				|| !StringUtils.isNotBlank(officialDocument.getPubOfficeInfo())) {
			officialDocument.setIsNormal(Constant.SITE_NOT_NORMAL);
		} else {
			officialDocument.setIsNormal(Constant.SITE_NORMAL);
		}
	}

	/**
	 * 此方法实现对政策日期和发布机关字段内容的提取 。
	 */
	public static void getPolicyDate(HtmlPage htmlPage, OfficialDocument officialDocument, String contentXpath,
			String policyDateXpath) {
		// 获取文本内容中的标签xpath,支持多模板
		String pXpath = contentXpath.concat(policyDateXpath.replaceAll(" +", "")).replaceAll("\\|",
				"\\|" + contentXpath);
		List<DomNode> contentHtml = htmlPage.getByXPath(pXpath);
		contentHtml = contentHtml.stream().filter(domNode -> {
			return !StringUtils.isBlank(domNode.asText());
		}).collect(toList());
		setDateOfficial(contentHtml, officialDocument);
	}

	/**
	 * 此方法实现对政策日期和发布机关字段内容的提取 。
	 */
	public static void getPolicyDate(WebDriver webDriver, OfficialDocument officialDocument, String contentXpath,
			String policyDateXpath) {
		// 获取文本内容中的标签xpath,支持多模板
		String pXpath = contentXpath.concat(policyDateXpath.replaceAll(" +", "")).replaceAll("\\|",
				"\\|" + contentXpath);
		List<WebElement> contentHtml = webDriver.findElements(By.xpath(pXpath));
		contentHtml = contentHtml.stream().filter(domNode -> {
			return !StringUtils.isBlank(domNode.getText());
		}).collect(toList());

		setDateOfficial(contentHtml, officialDocument);
	}

	public static void parseXMLList(List<DomNode> domElementList, ListDetail listDetail,
			List<OfficialDocument> officialDocuments) {
		if (!CollectionUtils.isEmpty(domElementList)) {
			domElementList.forEach(dom -> {
				if (dom != null) {
					DomCDataSection domAttr = (DomCDataSection) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					String href = element.getElementsByTag("a").get(0).attr("href");
					String name = element.getElementsByTag("a").get(0).text();
					if (!href.contains("http://")) {
						href = listDetail.getDocHost().concat(href);
					}
					if (!StringUtils.isEmpty(href)) {
						OfficialDocument officialDocument = new OfficialDocument();
						String title = name;
						if (StringUtils.isEmpty(name)) {
							Attributes attrs = element.attributes();
							if (attrs != null) {
								String node = attrs.get("title");
								if (node != null && !StringUtils.isBlank(node)) {
									title = node;
								}
							}
						}
						officialDocument.setUrl(href);
						officialDocument.setTitle(title);
						if (Optional.ofNullable(officialDocument).isPresent()) {
							officialDocuments.add(officialDocument);
						}
					}
				}
			});
		}
	}

	/**
	 * 此方法实现对HTML字符串对<script></script>过滤
	 */
	public static String delScript(String htmlStr) {
		String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤
		return htmlStr;
	}

	public static void parseHTMLList(List<DomNode> domElementList, ConfigListEntity configListEntity,
			List<TaskInfoEntity> taskInfoEntities, HtmlPage htmlPage) {
		if (!CollectionUtils.isEmpty(domElementList)) {
			domElementList.forEach(dom -> {
				if (dom != null) {
                    String title = "";
                    String href = "";
                    //处理网页为HtmlAchor对象的列表
                    if (dom instanceof HtmlAnchor) {
						href = ((HtmlAnchor) dom).getHrefAttribute();
						href = getUrl(href, htmlPage);
						if (!StringUtils.isEmpty(href)) {
							NamedNodeMap attrs = dom.getAttributes();
							if (attrs != null) {
								Node node = attrs.getNamedItem("title");
								if (node != null && !StringUtils.isBlank(node.getTextContent())) {
									title = node.getTextContent();
								}
							}
							if (!StringUtils.isBlank(title) && Util.hasHtmlTags(title)){
								title = dom.asText();
							}
							if(StringUtils.isBlank(title)){
								title = dom.asText();
							}
							if(!StringUtils.isBlank(configListEntity.getListIdUrl())){
							    if(!StringUtils.isBlank(configListEntity.getListJsonSubstring())){
                                    String[] jsonSubstrings = configListEntity.getListJsonSubstring().split(";");
                                    href = href.substring(href.indexOf(jsonSubstrings[0]) + 1, href.indexOf(jsonSubstrings[1]));
                                }
							    href = String.format(configListEntity.getListIdUrl(), href);
                            }
							/**
							 * author ：SUN
							 * 处理详情页url和列表页url不同的问题
							 */
							String docHref = configListEntity.getDocHref();
							if(!StringUtils.isBlank(docHref)){
								String code = href.split("=")[1];
								StringBuffer sb = new StringBuffer(docHref);
								sb.append(code).append(".html");
                                href = sb.toString();
							}


                            /**
                             * author ：SUN
                             * 处理域名和ip的问题
                             */
                            String docHost =  configListEntity.getDocHost();
                            if(!StringUtils.isBlank(docHost)) {
                                String host=null;
                                Integer port=0 ;
                                String http=null;
                                StringBuilder sb = new StringBuilder();
                                try {
                                    URL url = new URL(href);
                                    host = url.getHost();
                                    port = url.getPort();
                                    http = url.getProtocol();
                                } catch (MalformedURLException e) {
                                    LOG.error("解析正文url报错 {}",href);
                                }
                                String l = "://";
                                String f= ":";
                                sb.append(http).append(l).append(host).append(f).append(port);
                                String url =sb.toString();
                                href = StringUtils.replace(href,url,docHost);
                            }
						}
					}
                    //处理网页为DomCDataSection对象的列表
                    else if(dom instanceof DomCDataSection) {
                        DomCDataSection domAttr = (DomCDataSection) dom;
                        String html = domAttr.getData();
                        Element element = Jsoup.parse(html);
                        href = element.getElementsByTag("a").get(0).attr("href");
                        if (!StringUtils.isEmpty(href)) {
                            if (!href.contains("http://")) {
                                href = configListEntity.getDocHost().concat(href);
                            }
                            title = element.getElementsByTag("a").get(0).text();
                        }
                    }
					//处理网页为DomComment对象的列表
                    else if(dom instanceof DomComment	){
                        DomComment domAttr = (DomComment) dom;
                        String html= domAttr.getData();
                        Element element = Jsoup.parse(html);
                        href = element.getElementsByTag("a").get(0).attr("href");
                        if (!StringUtils.isEmpty(href)) {
                            if (!href.contains("http://")) {
                                href = configListEntity.getDocHost().concat(href);
                            }
                            title = element.getElementsByTag("a").get(0).attr("title");
                        }
                    }
                    //添加列表
                    if (!StringUtils.isEmpty(href) && !StringUtils.isBlank(title) ) {
						TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
						taskInfoEntity.setSectionUrl(href);
						taskInfoEntity.setSectionTitle(title);
                        if (Optional.ofNullable(configListEntity).isPresent()) {
							taskInfoEntities.add(taskInfoEntity);
                        }
                    }

				}
			});
		}
	}

	public static void parseWebDriverList(List<TaskInfoEntity> taskInfoEntities, ConfigListEntity configListEntity,
			WebDriver webDriver) throws UnsupportedEncodingException {
		try {
			waitByCondition(webDriver, ExpectedConditions.visibilityOfElementLocated(By.xpath(configListEntity.getListXpath())));
		} catch (Exception e) {
			LOG.error("未找到需要加载的元素",e);
			return;
		}
		List<WebElement> domNodeList = webDriver.findElements(By.xpath(configListEntity.getListXpath()));
		if (CollectionUtils.isNotEmpty(domNodeList)) {
			for (WebElement webElement : domNodeList) {
				TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
				String title = "";
				String href = webElement.getAttribute("href");
				if (StringUtils.isBlank(title)) {
					String att = webElement.getAttribute("title");
					if (!StringUtils.isBlank(att)) {
						title = att;
					}
				}
				if (StringUtils.isBlank(title)) {
					title = webElement.getText();
				}
				taskInfoEntity.setSectionUrl(href);
				taskInfoEntity.setSectionTitle(title);
				if (Optional.ofNullable(configListEntity).isPresent()) {
					taskInfoEntities.add(taskInfoEntity);
				}
			}
		}
	}

	public static  <T> T waitByCondition(WebDriver webDriver, ExpectedCondition<T> expectedCondition) {
		return new WebDriverWait(webDriver, 30).until(expectedCondition);
	}

	public static OfficialDocument saveHtmlToFile(OfficialDocument officialDocument,String rootPath) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdf2= new SimpleDateFormat("yyyyMM");
		//正文文字和正文html 存文件
		String textHtml = officialDocument.getTextHtml();
		String content = officialDocument.getContent();
		String insertDateString=officialDocument.getInsertDate();
		Date insertLocalDate=new Date();
		if (StringUtils.isNotBlank(insertDateString)){
			insertLocalDate = sdf1.parse(insertDateString);
		}
		String monthString = sdf2.format(insertLocalDate);

		String keyId = officialDocument.getKeyId();
		if (StringUtils.isBlank(keyId)){
			keyId=Util.getUUID();
            officialDocument.setKeyId(keyId);
		}

		String fileParePath=rootPath.concat(monthString).concat(File.separator).concat(keyId);
		String contentFileName=fileParePath.concat("_content.txt");
		String textHtmlFileName=fileParePath.concat("_html.txt");

		//写文件成功则将字段值设置为全路径
		Util.writeStringToFile(contentFileName,content);
		officialDocument.setContent(contentFileName);
		Util.writeStringToFile(textHtmlFileName,textHtml);
		officialDocument.setTextHtml(textHtmlFileName);

		return officialDocument;
	}


	public static void parseListByClick(ThreadLocal<WebDriver> webDriverThreadLocal, ConfigListEntity configListEntity,
										  WebDriver webDriver,List<TaskInfoEntity> taskInfoEntities, String sectionUrl) throws UnsupportedEncodingException {

		if(Optional.ofNullable(configListEntity.getListHref()).isPresent()) {
			Integer flage = configListEntity.getListHref();
			if (flage == 1) {
				taskInfoEntities.clear();
				webDriverThreadLocal.set(webDriver);
				webDriverThreadLocal.get().get(sectionUrl);
				List<WebElement> domNodeList = webDriver.findElements(By.xpath(configListEntity.getListXpath()));
				for (WebElement webElement : domNodeList) {
					TaskInfoEntity taskInfoEntity = new TaskInfoEntity();
					String title = webElement.getText();
					try {
						if (webElement.isEnabled() && webElement.isDisplayed()) {
							LOG.info("使用JS进行页面元素单击");
							//执行JS语句arguments[0].click();
							((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", webElement);
						} else {
							LOG.error("页面上元素无法进行单击操作");
						}
					} catch (StaleElementReferenceException e) {
						LOG.error("页面元素没有附加在页面中" + Arrays.toString(e.getStackTrace()));
					} catch (NoSuchElementException e) {
						LOG.error("在页面中没有找到要操作的元素" + Arrays.toString(e.getStackTrace()));
					} catch (Exception e) {
						LOG.error("无法完成单击操作" + Arrays.toString(e.getStackTrace()));
					}
					String currentWindow = webDriver.getWindowHandle();//获取当前窗口句柄
					Set<String> handles = webDriver.getWindowHandles();//获取所有窗口句柄
					Iterator<String> it = handles.iterator();
					String currentUrl = null;
					while (it.hasNext()) {
						if (currentWindow == it.next()) {
							continue;
						}
						WebDriver window = webDriver.switchTo().window(it.next());//切换到新窗口
						currentUrl = window.getCurrentUrl();
						window.close();//关闭新窗口
					}
					webDriver.switchTo().window(currentWindow);//回到原来页面
					taskInfoEntity.setSectionUrl(currentUrl);
					taskInfoEntity.setSectionTitle(title);
					taskInfoEntities.add(taskInfoEntity);
				}
				webDriver.quit();
			}
		}
	}

}
