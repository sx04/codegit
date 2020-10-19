package com.cetcbigdata.varanus.core.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.RedisBloomFilter;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.cetcbigdata.varanus.dao.ListDetailDAO;
import com.cetcbigdata.varanus.dao.TaskBasicInfoDAO;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.dao.TemplateDAO;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:16:00
 * 
 * @author matthew 列表解析器基类
 */
@Component
public abstract class BaseRequestDocListParser {

	private static final Logger LOG = LoggerFactory.getLogger(BaseRequestDocListParser.class);
	@Autowired
	WebClientPool webClientPool;
	@Autowired
	public RedisTemplate redisTemplate;
	@Autowired
	public RedisBloomFilter redisBloomFilter;
	@Autowired
	private TaskWarningDAO taskWarningDAO;
	@Autowired
	public CheckNetWork checkNetWork;
	@Autowired
	private ListDetailDAO listDetailDAO;
	@Autowired
	private TaskBasicInfoDAO taskBasicInfoDAO;

	@Autowired
	private TemplateDAO templateDAO;


	/**
	 * 解析文档列表
	 */
	//protected abstract void parseDocList(ThreadLocal<WebClient> webClient, ListDetail docList,
	//		TaskBasicInfo taskBasicInfo) throws Exception;

	/**
	 * 解析文档列表
	 */
	protected abstract void parseDocList(WebClient webClient, ConfigListEntity configListEntity,
										 TaskInfoEntity taskInfoEntity,TemplateEntity templateEntity) throws Exception;



	/**
	 * 解析需要用到模拟浏览器文档列表
	 */
	public void parseWebDocList(ThreadLocal<WebDriver> webDriverThreadLocal, ListDetail docList,
			TaskBasicInfo taskBasicInfo) throws Exception {

	}

	/*public abstract void processList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo)
			throws Exception;*/

	public abstract void processList(WebClient webClient, ConfigListEntity configListEntity,
									 TaskInfoEntity taskInfoEntity,TemplateEntity templateEntity)
			throws Exception;


	public void processWebList(ThreadLocal<WebDriver> webDriverThreadLocal, ListDetail docList,
			TaskBasicInfo taskBasicInfo) throws Exception {

	}

	/**
	 * 该方法主要通过遍历个分页获取url，然后传递给redis队列。分页中，通过循环判断domNodeList是否有值来判断页面是否有下一页。
	 */
	@Deprecated
	public void getHtmlPageList(ThreadLocal<WebClient> webClient, String index, ListDetail docList, String taskQueue,
			int startNum, RedisBloomFilter redisBloomFilter, String bloomFilterKey) {
		String url = "";
		HtmlPage htmlPage = null;
		String lastCrawlerUrlNew = null;
		TaskBasicInfo taskBasicInfo =  taskBasicInfoDAO.findByTaskId(docList.getTaskId());
		Boolean taskFlag = true;
		if (taskBasicInfo!=null){
			if (taskBasicInfo.getIsValid()!=null){
				taskFlag = taskBasicInfo.getIsValid()==1?true:false;
			}
		}
		for (int i = startNum; i <= docList.getListPageNumber() && taskFlag; i++) {
			taskBasicInfo =  taskBasicInfoDAO.findByTaskId(docList.getTaskId());
			if (taskBasicInfo!=null){
				if (taskBasicInfo.getIsValid()!=null){
					taskFlag = taskBasicInfo.getIsValid()==1?true:false;
				}
			}
			try {
				if (i == startNum) {
					url = index;
				} else {
					if (StringUtils.isEmpty(docList.getListYearPage())) {
						url = PageParserHelper.getFormatUrl(docList.getListTemplateUrl(), i);
					} else {
						url = PageParserHelper.getFormatUrl(docList.getListTemplateUrl(), docList.getListYearPage(), i);
					}
				}
				WebRequest webRequest = PageParserHelper.getWebRequest(url, HttpMethod.GET);
				// 获取到列表页的页面html对象
				htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, docList.getListNeedProxy()==1?true:false);
				if (htmlPage == null) {
					/*TaskWarning taskWarning = new TaskWarning();
					String taskp = PageParserHelper.disassembleQueue(taskQueue);
					Integer taskId = Integer.valueOf(taskp.split(":")[0]);
					taskWarning.setTaskId(taskId);
					taskWarning.setListWarningType(Constants.URL_WARNING);
					taskWarning.setListWarningDetail("url:" + url + "访问失败");
					taskWarningDAO.save(taskWarning);*/
				}
			} catch (Exception e) {
				LOG.error("使用get方式循环获取文章列表url出错--> {}, -->{}", url, e);
				continue;
			}
			List<DomNode> domNodeList = PageParserHelper.getDomNodeList(htmlPage, docList.getListXpath());
			if (domNodeList == null || domNodeList.size() <= 0) {
				/*TaskWarning taskWarning = new TaskWarning();
				String taskp = PageParserHelper.disassembleQueue(taskQueue);
				Integer taskId = Integer.valueOf(taskp.split(":")[0]);
				Integer listId = Integer.valueOf(taskp.split(":")[1]);
				taskWarning.setTaskId(taskId);
				taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
				taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "未匹配文章列表" + url);
				taskWarningDAO.save(taskWarning);*/
				LOG.error("从{}中,根据xpth{}未获取到文章列表", url, docList.getListXpath());
			} else {
				// 将url写入队列中以用来访问文章详情
				addQueueByDomNodeList(domNodeList, htmlPage, taskQueue, redisBloomFilter, bloomFilterKey,
						docList.getLastCrawlerUrl(), docList);
				if (i == startNum) {
					lastCrawlerUrlNew = getLastCrawlerUrl(domNodeList, htmlPage);
					if (!StringUtils.isBlank(docList.getListIdUrl()) && !StringUtils.isBlank(lastCrawlerUrlNew)) {
						if (!StringUtils.isBlank(docList.getListJsonSubstring())) {
							String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
							lastCrawlerUrlNew = lastCrawlerUrlNew.substring(lastCrawlerUrlNew.indexOf(jsonSubstrings[0]) + 1, lastCrawlerUrlNew.indexOf(jsonSubstrings[1]));
						}
						lastCrawlerUrlNew = String.format(docList.getListIdUrl(), lastCrawlerUrlNew);
					}
				}
				if (isLastCrawlerList(domNodeList, htmlPage, docList.getLastCrawlerUrl(), docList)) {
					break;
				}
			}
		}
		if (!StringUtils.isBlank(lastCrawlerUrlNew)
				&& (docList.getLastCrawlerUrl() == null || !docList.getLastCrawlerUrl().equals(lastCrawlerUrlNew))) {
			listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getListId());
		}
	}



	/**
	 * 该方法主要通过遍历个分页获取url，然后传递给redis队列。分页中，通过循环判断domNodeList是否有值来判断页面是否有下一页。
	 */
	public void getHtmlPageList(WebClient webClient, String index, ConfigListEntity configListEntity, String taskQueue,
								int startNum, RedisBloomFilter redisBloomFilter, String bloomFilterKey) {
		String url = "";
		HtmlPage htmlPage = null;
		String lastCrawlerUrlNew = null;
		//TemplateEntity templateEntity = templateDAO.findById(configListEntity.getTemplateId());
		TemplateEntity templateEntity = templateDAO.findOne(configListEntity.getTemplateId());
				Boolean taskFlag = true;
		if (templateEntity!=null){
			if (templateEntity!=null){
				taskFlag = templateEntity.getIsRun()==1?true:false;
			}
		}
		for (int i = startNum; i <= templateEntity.getListPageNumber() && taskFlag; i++) {
			templateEntity = templateDAO.findOne(configListEntity.getTemplateId());
			if (templateEntity!=null){
				if (templateEntity!=null){
					taskFlag = templateEntity.getIsRun()==1?true:false;
				}
			}
			try {
				if (i == startNum) {
					url = index;
				} else {
					if (StringUtils.isEmpty(configListEntity.getListYearPage())) {
						url = PageParserHelper.getFormatUrl(templateEntity.getListTemplateUrl(), i);
					} else {
						url = PageParserHelper.getFormatUrl(templateEntity.getListTemplateUrl(), configListEntity.getListYearPage(), i);
					}
				}
				String httpMethod = configListEntity.getListRequestType();
				WebRequest webRequest = PageParserHelper.getWebRequest(url, httpMethod.equalsIgnoreCase("GET") ? HttpMethod.GET : HttpMethod.POST);
				// 获取到列表页的页面html对象
				htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, configListEntity.getListNeedProxy() == 1 ? true : false);
			} catch (Exception e) {
				LOG.error("使用get方式循环获取文章列表url出错--> {}, -->{}", url, e);
				TaskWarningEntity taskWarningEntity = new TaskWarningEntity(templateEntity.getId(),Constants.URL_WARNING,"url:" + url + "访问失败"+"原因:"+e.getCause());
				taskWarningDAO.save(taskWarningEntity);
				continue;
			}
			List<DomNode> domNodeList = PageParserHelper.getDomNodeList(htmlPage, configListEntity.getListXpath());
			if (domNodeList == null || domNodeList.size() <= 0) {
				String taskp = PageParserHelper.disassembleQueue(taskQueue);
				Integer templateId = Integer.valueOf(taskp.split(":")[1]);
				TaskWarningEntity taskWarningEntity = new TaskWarningEntity(templateEntity.getId(),Constants.TASK_TEMPLATE_WARNING,"任务list模板，templateId:" + templateId + "未匹配文章列表" + url);
				taskWarningDAO.save(taskWarningEntity);
				LOG.error("从{}中,根据xpth{}未获取到文章列表", url, configListEntity.getListXpath());
			} else {
				// 将url写入队列中以用来访问文章详情
				addQueueByDomNodeList(domNodeList, htmlPage, taskQueue, redisBloomFilter, bloomFilterKey,
						configListEntity.getLastCrawlerUrl(), configListEntity);
				if (i == startNum) {
					lastCrawlerUrlNew = getLastCrawlerUrl(domNodeList, htmlPage);
					if (!StringUtils.isBlank(configListEntity.getListIdUrl()) && !StringUtils.isBlank(lastCrawlerUrlNew)) {
						if (!StringUtils.isBlank(configListEntity.getListJsonSubstring())) {
							String[] jsonSubstrings = configListEntity.getListJsonSubstring().split(";");
							lastCrawlerUrlNew = lastCrawlerUrlNew.substring(lastCrawlerUrlNew.indexOf(jsonSubstrings[0]) + 1, lastCrawlerUrlNew.indexOf(jsonSubstrings[1]));
						}
						lastCrawlerUrlNew = String.format(configListEntity.getListIdUrl(), lastCrawlerUrlNew);
					}
				}
				if (isLastCrawlerList(domNodeList, htmlPage, configListEntity.getLastCrawlerUrl(), configListEntity)) {
					break;
				}
			}
		}
		if (!StringUtils.isBlank(lastCrawlerUrlNew)
				&& (configListEntity.getLastCrawlerUrl() == null || !configListEntity.getLastCrawlerUrl().equals(lastCrawlerUrlNew))) {
			//listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, configListEntity.getId());
		}
	}



















	public void getWebDriverList(ThreadLocal<WebDriver> webDriverThreadLocal, String index, ListDetail docList,
			String taskQueue, int startNum, RedisBloomFilter redisBloomFilter, String bloomFilterKey) {
		String url = "";
		String lastCrawlerUrlNew = null;
		TaskBasicInfo taskBasicInfo =  taskBasicInfoDAO.findByTaskId(docList.getTaskId());
		Boolean taskFlag = true;
		if (taskBasicInfo!=null){
			if (taskBasicInfo.getIsValid()!=null){
				taskFlag = taskBasicInfo.getIsValid()==1?true:false;
			}
		}

		if(!StringUtils.isEmpty(docList.getListDriverPageJs())){
			startNum = 1;
		}
		for (int i = startNum; i <= docList.getListPageNumber() && taskFlag; i++) {
			taskBasicInfo =  taskBasicInfoDAO.findByTaskId(docList.getTaskId());
			if (taskBasicInfo!=null){
				if (taskBasicInfo.getIsValid()!=null){
					taskFlag = taskBasicInfo.getIsValid()==1?true:false;
				}
			}
			try {
				if (i == startNum) {
					url = index;
				} else {
					if (StringUtils.isEmpty(docList.getListYearPage())) {
						url = PageParserHelper.getFormatUrl(docList.getListTemplateUrl(), i);
					} else {
						url = PageParserHelper.getFormatUrl(docList.getListTemplateUrl(), docList.getListYearPage(), i);
					}
				}
				// 获取到列表页的页面html对象
				WebDriver webDriver = PageParserHelper.getWebDriverPage(webDriverThreadLocal, url, docList.getListNeedProxy()==1?true:false);
				if (webDriver == null) {
					/*TaskWarning taskWarning = new TaskWarning();
					String taskp = PageParserHelper.disassembleQueue(taskQueue);
					Integer taskId = Integer.valueOf(taskp.split(":")[0]);
					taskWarning.setTaskId(taskId);
					taskWarning.setListWarningType(Constants.URL_WARNING);
					taskWarning.setListWarningDetail("url:" + url + "访问失败");
					taskWarningDAO.save(taskWarning);*/
				}
				if (i > docList.getListPageNumber()) {
					LOG.info("{}分页请求结束", url);
					break;
				}
				if(!StringUtils.isEmpty(docList.getListDriverPageJs())){
                    JavascriptExecutor jse = (JavascriptExecutor)webDriver;
                    jse.executeScript(String.format(docList.getListDriverPageJs(), i));
                }
				try {
					PageParserHelper.waitByCondition(webDriver, ExpectedConditions.visibilityOfElementLocated(By.xpath(docList.getListXpath())));
				} catch (Exception e) {
					LOG.error("未找到需要加载的元素",e);
					return;
				}
                List<WebElement> domNodeList = webDriver.findElements(By.xpath(docList.getListXpath()));
				if (domNodeList == null || domNodeList.size() <= 0) {
					/*TaskWarning taskWarning = new TaskWarning();
					String taskp = PageParserHelper.disassembleQueue(taskQueue);
					Integer taskId = Integer.valueOf(taskp.split(":")[0]);
					Integer listId = Integer.valueOf(taskp.split(":")[1]);
					taskWarning.setTaskId(taskId);
					taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
					taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "未匹配文章列表" + url);
					taskWarningDAO.save(taskWarning);*/
					LOG.error("从{}中,根据xpth{}未获取到文章列表", url, docList.getListXpath());
				} else {
					int j = 1;
					// 将url写入队列中以用来访问文章详情
					for (WebElement webElement : domNodeList) {
						OfficialDocument listDetail = new OfficialDocument();
						String title = "";
						String href = webElement.getAttribute("href");
						if (StringUtils.isBlank(title)) {
							String att = webElement.getAttribute("title");
							if (!StringUtils.isBlank(att)) {
								title = att;
							}
						}
						if (StringUtils.isBlank(title)){
							title = webElement.getText();
						}
						listDetail.setUrl(href);
						listDetail.setTitle(title);
						if (Optional.ofNullable(listDetail).isPresent()) {
							if (i == startNum && j == 1) {
								lastCrawlerUrlNew = listDetail.getUrl();
							}
							addQueue(taskQueue, listDetail, redisBloomFilter, bloomFilterKey);
							j++;
						}
					}

					if (isLastCrawlerList(domNodeList, webDriverThreadLocal.get(), docList.getLastCrawlerUrl())) {
						break;
					}
				}
				if (lastCrawlerUrlNew != null
						&& (docList.getLastCrawlerUrl() == null || !docList.getLastCrawlerUrl().equals(lastCrawlerUrlNew))) {
					listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getListId());
				}
			} catch (Exception e) {
				LOG.error("使用webdriver方式循环获取文章列表url出错--> {}, -->{}", url, e);
			}
		}

	}

	/**
	 * okhttp json返回的即为文档详情的信息，只需要解析文档信息即可完成
	 */
	@Deprecated
	public void getHttpList(ThreadLocal<WebClient> webClient, String taskQueue, JSONObject jsonField, ListDetail docList,
			RedisBloomFilter redisBloomFilter, String bloomFilterKey) throws Exception {
		String jsonKey = docList.getListJsonKey();
		String jsonSubstring = docList.getListJsonSubstring();
		String listIdUrl = docList.getListIdUrl();
		String idKey = docList.getJsonIdKey();
		Integer listPageNumber = docList.getListPageNumber();
		String pageTemplate = docList.getListTemplateUrl();
		String lastCrawlerUrl = docList.getLastCrawlerUrl();
		Boolean isLast = false;
		String lastCrawlerUrlNew = null;
		for (int i = 1; i <= listPageNumber; i++) {
			String url = pageTemplate.replace("%s", String.valueOf(i));
			WebRequest webRequest = null;
			if (docList.getListRequestType().equals(HttpMethod.GET.name())) {
				webRequest = PageParserHelper.getWebRequest(url, HttpMethod.GET);
			} else if (docList.getListRequestType().equals(HttpMethod.POST.name())) {
				if (!StringUtils.isBlank(docList.getListRequestParams())) {
					webRequest = PageParserHelper.getWebRequest(url, HttpMethod.POST);
					PageParserHelper.jsonPairsToMap(docList.getListRequestParams(), webRequest);
				}
			}
			//增加浏览器头部信息
			if (!StringUtils.isBlank(docList.getListPageHeader())){
				PageParserHelper.jsonHeaders(docList.getListPageHeader(),webRequest);
			}
			HtmlPage page = null;
			page = PageParserHelper.getHtmlPage(webClient, webRequest, docList.getListNeedProxy()==1?true:false);
			if (Optional.ofNullable(page).isPresent() && page.getWebResponse().getStatusCode()==200) {
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
				int j = 1;
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
						if(id.contains("./")){
							officialDocument.setUrl(PageParserHelper.getUrl(id,page));
						}else {
							officialDocument.setUrl(docUrl.replace("%s", id));
						}
					}
					if (!StringUtils.isEmpty(officialDocument.getUrl())
							&& StringUtils.equals(officialDocument.getUrl(), lastCrawlerUrl)) {
						isLast = true;
						break;
					}
					if (!StringUtils.isBlank(officialDocument.getTitle())
							&& !StringUtils.equals(officialDocument.getUrl(), "链接")
							&& !StringUtils.isBlank(officialDocument.getUrl())) {
						addQueue(taskQueue, officialDocument, redisBloomFilter, bloomFilterKey);
						if (i == 1 && j == 1) {
							lastCrawlerUrlNew = officialDocument.getUrl();
						}
						j++;
					} else {
						/*TaskWarning taskWarning = new TaskWarning();
						String taskp = PageParserHelper.disassembleQueue(taskQueue);
						Integer taskId = Integer.valueOf(taskp.split(":")[0]);
						Integer listId = Integer.valueOf(taskp.split(":")[1]);
						taskWarning.setTaskId(taskId);
						taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
						taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "未匹配文章列表" + url);
						taskWarningDAO.save(taskWarning);*/
					}
				}
			} else {
				/*TaskWarning taskWarning = new TaskWarning();
				String taskp = PageParserHelper.disassembleQueue(taskQueue);
				Integer taskId = Integer.valueOf(taskp.split(":")[0]);
				taskWarning.setTaskId(taskId);
				taskWarning.setListWarningType(Constants.URL_WARNING);
				taskWarning.setListWarningDetail("url:" + url + "访问失败");
				taskWarningDAO.save(taskWarning);*/
			}
			if (isLast) {
				break;
			}
		}
		if (lastCrawlerUrlNew != null
				&& (docList.getLastCrawlerUrl() == null || docList.getLastCrawlerUrl() != lastCrawlerUrlNew)) {
			listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getListId());
		}

	}


	/**
	 * okhttp json返回的即为文档详情的信息，只需要解析文档信息即可完成
	 */
	public void getHttpList(ThreadLocal<WebClient> webClient, String taskQueue, JSONObject jsonField, ConfigListEntity docList,TemplateEntity templateEntity,
							RedisBloomFilter redisBloomFilter, String bloomFilterKey) throws Exception {
		String jsonKey = docList.getListJsonKey();
		String jsonSubstring = docList.getListJsonSubstring();
		String listIdUrl = docList.getListIdUrl();
		String idKey = docList.getJsonIdKey();
		Integer listPageNumber = templateEntity.getListPageNumber();
		String pageTemplate = templateEntity.getListTemplateUrl();
		String lastCrawlerUrl = docList.getLastCrawlerUrl();
		Boolean isLast = false;
		String lastCrawlerUrlNew = null;
		for (int i = 1; i <= listPageNumber; i++) {
			String url = pageTemplate.replace("%s", String.valueOf(i));
			String httpMethod = docList.getListRequestType();
			WebRequest webRequest = PageParserHelper.getWebRequest(url, httpMethod.equalsIgnoreCase("GET") ? HttpMethod.GET : HttpMethod.POST);
			if (!StringUtils.isBlank(docList.getListRequestParams())) {
				webRequest = PageParserHelper.getWebRequest(url, HttpMethod.POST);
				PageParserHelper.jsonPairsToMap(docList.getListRequestParams(), webRequest);
			}
			//增加浏览器头部信息
			if (!StringUtils.isBlank(docList.getListPageHeader())){
				PageParserHelper.jsonHeaders(docList.getListPageHeader(),webRequest);
			}
			HtmlPage page = null;
			page = PageParserHelper.getHtmlPage(webClient, webRequest, docList.getListNeedProxy()==1?true:false);
			if (Optional.ofNullable(page).isPresent() && page.getWebResponse().getStatusCode()==200) {
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
				int j = 1;
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
						if(id.contains("./")){
							officialDocument.setUrl(PageParserHelper.getUrl(id,page));
						}else {
							officialDocument.setUrl(docUrl.replace("%s", id));
						}
					}
					if (!StringUtils.isEmpty(officialDocument.getUrl())
							&& StringUtils.equals(officialDocument.getUrl(), lastCrawlerUrl)) {
						isLast = true;
						break;
					}
					if (!StringUtils.isBlank(officialDocument.getTitle())
							&& !StringUtils.equals(officialDocument.getUrl(), "链接")
							&& !StringUtils.isBlank(officialDocument.getUrl())) {
						addQueue(taskQueue, officialDocument, redisBloomFilter, bloomFilterKey);
						if (i == 1 && j == 1) {
							lastCrawlerUrlNew = officialDocument.getUrl();
						}
						j++;
					} else {
						/*TaskWarning taskWarning = new TaskWarning();
						String taskp = PageParserHelper.disassembleQueue(taskQueue);
						Integer taskId = Integer.valueOf(taskp.split(":")[0]);
						Integer listId = Integer.valueOf(taskp.split(":")[1]);
						taskWarning.setTaskId(taskId);
						taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
						taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "未匹配文章列表" + url);
						taskWarningDAO.save(taskWarning);*/
					}
				}
			} else {
				/*TaskWarning taskWarning = new TaskWarning();
				String taskp = PageParserHelper.disassembleQueue(taskQueue);
				Integer taskId = Integer.valueOf(taskp.split(":")[0]);
				taskWarning.setTaskId(taskId);
				taskWarning.setListWarningType(Constants.URL_WARNING);
				taskWarning.setListWarningDetail("url:" + url + "访问失败");
				taskWarningDAO.save(taskWarning);*/
			}
			if (isLast) {
				break;
			}
		}
		if (lastCrawlerUrlNew != null
				&& (docList.getLastCrawlerUrl() == null || docList.getLastCrawlerUrl() != lastCrawlerUrlNew)) {
			listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getId());
		}

	}


	public void postHtmlPageList(ThreadLocal<WebClient> webClient, String index, ListDetail docList, String taskQueue,
			int startNum, RedisBloomFilter redisBloomFilter, String bloomFilterKey) {
		int perPageSize = 1; //网站每页的url数量
		int perUrlSize = 1; //每次请求url获取的数量
		String pageNamePattern = ".*start.*";
		if(Pattern.matches(pageNamePattern, docList.getListPageName())) {
			perPageSize = 50;
			perUrlSize = 100;
		}
		HtmlPage htmlPage = null;
		String lastCrawlerUrlNew = null;
		try {
			String lastCrawlerUrl = docList.getLastCrawlerUrl();
			WebRequest webRequest = PageParserHelper.getWebRequest(index, HttpMethod.POST);
			if (!StringUtils.isBlank(docList.getListRequestParams())) {
				PageParserHelper.jsonPairsToMap(docList.getListRequestParams(), webRequest);
			}
			if (!StringUtils.isBlank(docList.getListPageHeader())){
				PageParserHelper.jsonHeaders(docList.getListPageHeader(),webRequest);
			}
			List<DomNode> domNodeList = null;
			Integer sum = docList.getListPageNumber()*perPageSize + startNum;//总数量

				for (int i = startNum; i <= sum; i = i + perUrlSize) {
				try {
					if (Optional.ofNullable(docList.getListPageName()).isPresent()) {
						if(i == startNum) {
							// 第一次删除已存的起始页码参数
							webRequest.getRequestParameters().remove(new NameValuePair(docList.getListPageName(), "" + (i)));

						} else {
							// 删除上一次起始页码参数
							webRequest.getRequestParameters().remove(new NameValuePair(docList.getListPageName(), "" + (i - perUrlSize)));
						}
						// 添加新的起始页码参数
						webRequest.getRequestParameters().add(new NameValuePair(docList.getListPageName(), "" + i));
					}

					// 获取到列表页的页面html对象
					htmlPage = PageParserHelper.getHtmlPage(webClient, webRequest, docList.getListNeedProxy()==1?true:false);
					if (htmlPage == null) {
						/*TaskWarning taskWarning = new TaskWarning();
						String taskp = PageParserHelper.disassembleQueue(taskQueue);
						Integer taskId = Integer.valueOf(taskp.split(":")[0]);
						taskWarning.setTaskId(taskId);
						taskWarning.setListWarningType(Constants.URL_WARNING);
						taskWarning.setListWarningDetail("url:" + index + "访问失败");
						taskWarningDAO.save(taskWarning);*/
					}
					if (htmlPage != null) {
						domNodeList = PageParserHelper.getDomNodeList(htmlPage, docList.getListXpath());
						if (domNodeList == null || domNodeList.size() <= 0) {
							/*TaskWarning taskWarning = new TaskWarning();
							String taskp = PageParserHelper.disassembleQueue(taskQueue);
							Integer taskId = Integer.valueOf(taskp.split(":")[0]);
							Integer listId = Integer.valueOf(taskp.split(":")[1]);
							taskWarning.setTaskId(taskId);
							taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
							taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "第" + i + "页未匹配文章列表" + index);
							taskWarningDAO.save(taskWarning);*/
							LOG.error("从{}根据xpth{}发送post请求第{}页未获取到文章列表", index, docList.getListXpath(), i);
						} else {
							// 将url写入队列中以用来访问文章详情
							addQueueByDomNodeList(domNodeList, htmlPage, taskQueue, redisBloomFilter, bloomFilterKey,
									lastCrawlerUrl, docList);
							if (i == startNum) {
								lastCrawlerUrlNew = getLastCrawlerUrl(domNodeList, htmlPage);
								if (!StringUtils.isBlank(docList.getListIdUrl()) && !StringUtils.isBlank(lastCrawlerUrlNew)) {
									if (!StringUtils.isBlank(docList.getListJsonSubstring())) {
										String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
										lastCrawlerUrlNew = lastCrawlerUrlNew.substring(lastCrawlerUrlNew.indexOf(jsonSubstrings[0]) + 1, lastCrawlerUrlNew.indexOf(jsonSubstrings[1]));
									}
									lastCrawlerUrlNew = String.format(docList.getListIdUrl(), lastCrawlerUrlNew);
								}
							}
						}
					}
					if (isLastCrawlerList(domNodeList, htmlPage, lastCrawlerUrl, docList)) {
						break;
					}
				}catch (Exception e){
					LOG.error("获取列表出错,url {} {}",index,e);
				}
			}
			if (lastCrawlerUrlNew != null
					&& (docList.getLastCrawlerUrl() == null || docList.getLastCrawlerUrl() != lastCrawlerUrlNew)) {
				listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getListId());
			}
		} catch (MalformedURLException e) {
			LOG.error("{} url  error  {}", index, e.getMessage());
		}

	}

	public void postXMLPageList(ThreadLocal<WebClient> webClient, String index, ListDetail docList, String taskQueue,
			int startNum, RedisBloomFilter redisBloomFilter, String bloomFilterKey) {
		int perPageSize = 50; //网站每页的url数量
		int perUrlSize = 100; //每次请求url获取的数量
		XmlPage xmlPage = null;
		String lastCrawlerUrlNew = null;
		try {
			WebRequest webRequest = PageParserHelper.getWebRequest(index, HttpMethod.POST);
			if (!StringUtils.isBlank(docList.getListRequestParams())) {
				PageParserHelper.jsonPairsToMap(docList.getListRequestParams(), webRequest);
			}
			Integer sum = docList.getListPageNumber()*perPageSize;//总数量
			for (int i = startNum; i <= sum; i = i + perUrlSize) {

				if (Optional.ofNullable(docList.getListPageName()).isPresent()) {
					if(i == startNum) {
						// 删除第一页页码参数
						webRequest.getRequestParameters()
								.remove(new NameValuePair(docList.getListPageName(), "" + (i)));
					} else {
						// 删除以前的起始页码参数
						webRequest.getRequestParameters()
								.remove(new NameValuePair(docList.getListPageName(), "" + (i - perUrlSize)));
					}
					// 添加新的起始页码参数
					webRequest.getRequestParameters()
							.add(new NameValuePair(docList.getListPageName(), String.valueOf(""+i)));
				}
				// 获取到列表页的页面html对象
				xmlPage = PageParserHelper.getXmlPage(webClient, webRequest, docList.getListNeedProxy()==1?true:false);
				if (xmlPage == null) {
					/*TaskWarning taskWarning = new TaskWarning();
					String taskp = PageParserHelper.disassembleQueue(taskQueue);
					Integer taskId = Integer.valueOf(taskp.split(":")[0]);
					taskWarning.setTaskId(taskId);
					taskWarning.setListWarningType(Constants.URL_WARNING);
					taskWarning.setListWarningDetail("url:" + index + "访问失败");
					taskWarningDAO.save(taskWarning);*/
				}
				if (xmlPage != null) {
					List<DomNode> domNodes = xmlPage.getByXPath(docList.getListXpath());
					if (domNodes == null || domNodes.size() <= 0) {
						/*TaskWarning taskWarning = new TaskWarning();
						String taskp = PageParserHelper.disassembleQueue(taskQueue);
						Integer taskId = Integer.valueOf(taskp.split(":")[0]);
						Integer listId = Integer.valueOf(taskp.split(":")[1]);
						taskWarning.setTaskId(taskId);
						taskWarning.setListWarningType(Constants.TASK_TEMPLATE_WARNING);
						taskWarning.setListWarningDetail("任务list模板，list_id:" + listId + "第" + i + "页未匹配文章列表" + index);
						taskWarningDAO.save(taskWarning);*/
						LOG.error("从{}根据xpth{}发送post请求第{}页未获取到文章列表", index, docList.getListXpath(), i);
					} else {
						if (i == startNum) {
							lastCrawlerUrlNew = getLastCrawlerUrlXml(domNodes, docList);
						}
						// 将url写入队列中以用来访问文章详情
						if (!addQueueByDomNodeList(domNodes, taskQueue, redisBloomFilter, bloomFilterKey, docList)) {
							break;
						}
					}
				}

			}
			if (lastCrawlerUrlNew != null
					&& (docList.getLastCrawlerUrl() == null || docList.getLastCrawlerUrl() != lastCrawlerUrlNew)) {
				listDetailDAO.setLastCrawlerUrlFor(lastCrawlerUrlNew, docList.getListId());
			}
		} catch (MalformedURLException e) {
			LOG.error("{} url  error  {}", index, e.getMessage());
		}

	}

	@Deprecated
	public void addQueueByDomNodeList(List<DomNode> domNodeList, HtmlPage htmlPage, String taskQueue,
			RedisBloomFilter redisBloomFilter, String bloomFilterKey, String lastCrawlerUrl, ListDetail docList) {
		if (domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return;
		}
		for (DomNode dom : domNodeList) {
			if (dom != null) {
				String title = "";
				String href = "";
				if (dom instanceof HtmlAnchor) {
					href = ((HtmlAnchor) dom).getHrefAttribute();
					href = PageParserHelper.getUrl(href, htmlPage);
					if (href.contains(";jsessionid=")){
						href = href.replace(StringUtils.substringBetween(href,"initSnThreePageArticle.do","?"),"");
					}
					if (!StringUtils.isEmpty(href)) {
						if(!StringUtils.isBlank(docList.getListIdUrl())){
							if(!StringUtils.isBlank(docList.getListJsonSubstring())){
								String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
								href = href.substring(href.indexOf(jsonSubstrings[0]) + 1, href.indexOf(jsonSubstrings[1]));
							}
							href = String.format(docList.getListIdUrl(), href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							break;
						}
						NamedNodeMap attrs = dom.getAttributes();
						if (attrs != null) {
							Node node = attrs.getNamedItem("title");
							if (node != null && !StringUtils.isBlank(node.getTextContent())) {
								title = node.getTextContent();
							}
						}
						if(StringUtils.isBlank(title)){
							title = dom.asText();
						}
					}
				}  else if(dom instanceof DomCDataSection) {
					DomCDataSection domAttr = (DomCDataSection) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
                        if (!href.contains("http://")) {
                            href = docList.getDocHost().concat(href);
                        }
                        if (StringUtils.equals(href, docList.getLastCrawlerUrl())) {
                            break;
                        }
                        title = element.getElementsByTag("a").get(0).text();
                    }
				} else if(dom instanceof DomComment) {
					DomComment domAttr = (DomComment) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://")) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, docList.getLastCrawlerUrl())) {
							break;
						}
						title = element.getElementsByTag("a").get(0).attr("title");
					}
				}
				if (!StringUtils.isEmpty(href) && !StringUtils.isBlank(title) ) {
					OfficialDocument listDetail = new OfficialDocument();
					listDetail.setUrl(href);
					listDetail.setTitle(title);
					if (Optional.ofNullable(listDetail).isPresent()) {
						addQueue(taskQueue, listDetail, redisBloomFilter, bloomFilterKey);
					}
				}
			}
		}
	}


	public void addQueueByDomNodeList(List<DomNode> domNodeList, HtmlPage htmlPage, String taskQueue,
									  RedisBloomFilter redisBloomFilter, String bloomFilterKey, String lastCrawlerUrl, ConfigListEntity docList) {
		if (domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return;
		}
		for (DomNode dom : domNodeList) {
			if (dom != null) {
				String title = "";
				String href = "";
				if (dom instanceof HtmlAnchor) {
					href = ((HtmlAnchor) dom).getHrefAttribute();
					href = PageParserHelper.getUrl(href, htmlPage);
					if (href.contains(";jsessionid=")){
						href = href.replace(StringUtils.substringBetween(href,"initSnThreePageArticle.do","?"),"");
					}
					if (!StringUtils.isEmpty(href)) {
						if(!StringUtils.isBlank(docList.getListIdUrl())){
							if(!StringUtils.isBlank(docList.getListJsonSubstring())){
								String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
								href = href.substring(href.indexOf(jsonSubstrings[0]) + 1, href.indexOf(jsonSubstrings[1]));
							}
							href = String.format(docList.getListIdUrl(), href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							break;
						}
						NamedNodeMap attrs = dom.getAttributes();
						if (attrs != null) {
							Node node = attrs.getNamedItem("title");
							if (node != null && !StringUtils.isBlank(node.getTextContent())) {
								title = node.getTextContent();
							}
						}
						if(StringUtils.isBlank(title)){
							title = dom.asText();
						}
					}
				}  else if(dom instanceof DomCDataSection) {
					DomCDataSection domAttr = (DomCDataSection) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://")) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, docList.getLastCrawlerUrl())) {
							break;
						}
						title = element.getElementsByTag("a").get(0).text();
					}
				} else if(dom instanceof DomComment) {
					DomComment domAttr = (DomComment) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://")) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, docList.getLastCrawlerUrl())) {
							break;
						}
						title = element.getElementsByTag("a").get(0).attr("title");
					}
				}
				if (!StringUtils.isEmpty(href) && !StringUtils.isBlank(title) ) {
					Map<String,Object> map = new LinkedHashMap<String,Object>();
					map.put("url",href);
					map.put("title",title);
					if (!map.isEmpty()) {
						addQueue(taskQueue, map, redisBloomFilter, bloomFilterKey);
					}
				}
			}
		}
	}







	public boolean addQueueByDomNodeList(List<DomNode> domNodeList, String taskQueue, RedisBloomFilter redisBloomFilter,
			String bloomFilterKey, ListDetail docList) {
		for (DomNode e : domNodeList) {
			DomCDataSection domAttr = (DomCDataSection) e;
			String html = domAttr.getData();
			Element element = Jsoup.parse(html);
			String href = element.getElementsByTag("a").get(0).attr("href");
			if (!href.contains("http://")) {
				href = docList.getDocHost().concat(href);
			}
			String name = element.getElementsByTag("a").get(0).text();
			OfficialDocument officialDocument = new OfficialDocument();
			officialDocument.setTitle(name);
			officialDocument.setUrl(href);
			if (StringUtils.equals(href, docList.getLastCrawlerUrl())) {
				return false;
			}
			if (Optional.ofNullable(officialDocument).isPresent()) {
				addQueue(taskQueue, officialDocument, redisBloomFilter, bloomFilterKey);
			}
			//return true;
		}
		return true;
	}

	@Deprecated
	public void addQueue(String taskQueue, OfficialDocument listDetail, RedisBloomFilter redisBloomFilter,
			String bloomFilterKey) {
		if (listDetail != null) {
			String url = listDetail.getUrl();
			if (redisBloomFilter.contains(bloomFilterKey, url)) {
				LOG.info("this task -->{} url has been crawled {}",bloomFilterKey, listDetail.getUrl());
				return;
			} else {
				redisTemplate.opsForList().leftPush(taskQueue, listDetail);
				redisBloomFilter.add(bloomFilterKey, url);
			}
		}
	}


	public void addQueue(String taskQueue, Map listDetail, RedisBloomFilter redisBloomFilter,
						 String bloomFilterKey) {
		if (listDetail != null) {
			String url = (String) listDetail.get("url");
			if (redisBloomFilter.contains(bloomFilterKey, url)) {
				LOG.info("this task -->{} url has been crawled {}",bloomFilterKey, url);
				return;
			} else {
				redisTemplate.opsForList().leftPush(taskQueue, listDetail);
				redisBloomFilter.add(bloomFilterKey, url);
			}
		}
	}


	/*
	 * public ListDetail getOfficialDocumentList(Element e, String baeUrl, String
	 * xpath) { String href = PageParserHelper.getAbsUrl(e,baeUrl); if
	 * (!StringUtils.isEmpty(href)) { ListDetail listDetail = new ListDetail();
	 * return listDetail; } return null; }
	 */

	public OfficialDocument setDocListDetail(String href, HtmlPage htmlPage, String xpath) {
		href = PageParserHelper.getUrl(href, htmlPage);
		if (!StringUtils.isEmpty(href)) {
			DomNode dom = htmlPage.getFirstByXPath(xpath);
			OfficialDocument listDetail = new OfficialDocument();
			String title = dom.getTextContent();
			NamedNodeMap attrs = dom.getAttributes();
			if (attrs != null) {
				Node node = attrs.getNamedItem("title");
				if (node != null && !StringUtils.isBlank(node.getTextContent())) {
					title = node.getTextContent();
				}
			}
			listDetail.setUrl(href);
			listDetail.setTitle(title);
			return listDetail;
		}
		return null;
	}

	WebClient getWebclient(Boolean needProxy) {
		return webClientPool.getFromPool(needProxy);
	}

	@Deprecated
	private Boolean isLastCrawlerList(List<DomNode> domNodeList, HtmlPage htmlPage, String lastCrawlerUrl, ListDetail docList) {
		if (lastCrawlerUrl == null || domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return false;
		}
		for (DomNode dom : domNodeList) {
			if (dom != null) {
				if (dom instanceof HtmlAnchor) {
					String href = ((HtmlAnchor) dom).getHrefAttribute();
					href = PageParserHelper.getUrl(href, htmlPage);
					if (!StringUtils.isEmpty(href)) {
						if(!StringUtils.isBlank(docList.getListIdUrl())){
							if(!StringUtils.isBlank(docList.getListJsonSubstring())){
								String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
								href = href.substring(href.indexOf(jsonSubstrings[0]) + 1, href.indexOf(jsonSubstrings[1]));
							}
							href = String.format(docList.getListIdUrl(), href);
							if (href.contains(";jsessionid=")){
								href = href.replace(StringUtils.substringBetween(href,"initSnThreePageArticle.do","?"),"");
							}
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}
					}
				}
				else if(dom instanceof DomCDataSection) {
					DomCDataSection domAttr = (DomCDataSection) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					String href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://") && docList.getDocHost()!=null) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}

					}
				} else if(dom instanceof DomComment) {
					DomComment domAttr = (DomComment) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					String href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://") && docList.getDocHost()!=null) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Boolean isLastCrawlerList(List<DomNode> domNodeList, HtmlPage htmlPage, String lastCrawlerUrl, ConfigListEntity docList) {
		if (lastCrawlerUrl == null || domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return false;
		}
		for (DomNode dom : domNodeList) {
			if (dom != null) {
				if (dom instanceof HtmlAnchor) {
					String href = ((HtmlAnchor) dom).getHrefAttribute();
					href = PageParserHelper.getUrl(href, htmlPage);
					if (!StringUtils.isEmpty(href)) {
						if(!StringUtils.isBlank(docList.getListIdUrl())){
							if(!StringUtils.isBlank(docList.getListJsonSubstring())){
								String[] jsonSubstrings = docList.getListJsonSubstring().split(";");
								href = href.substring(href.indexOf(jsonSubstrings[0]) + 1, href.indexOf(jsonSubstrings[1]));
							}
							href = String.format(docList.getListIdUrl(), href);
							if (href.contains(";jsessionid=")){
								href = href.replace(StringUtils.substringBetween(href,"initSnThreePageArticle.do","?"),"");
							}
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}
					}
				}
				else if(dom instanceof DomCDataSection) {
					DomCDataSection domAttr = (DomCDataSection) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					String href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://") && docList.getDocHost()!=null) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}

					}
				} else if(dom instanceof DomComment) {
					DomComment domAttr = (DomComment) dom;
					String html = domAttr.getData();
					Element element = Jsoup.parse(html);
					String href = element.getElementsByTag("a").get(0).attr("href");
					if (!StringUtils.isEmpty(href)) {
						if (!href.contains("http://") && docList.getDocHost()!=null) {
							href = docList.getDocHost().concat(href);
						}
						if (StringUtils.equals(href, lastCrawlerUrl)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}




	private String getLastCrawlerUrl(List<DomNode> domNodeList, HtmlPage htmlPage) {
		if (domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return null;
		}
		for (DomNode dom : domNodeList) {
			if (dom != null) {
				if (dom instanceof HtmlAnchor) {
					String href = ((HtmlAnchor) dom).getHrefAttribute();
					href = PageParserHelper.getUrl(href, htmlPage);
					if (!StringUtils.isEmpty(href)) {
						return href;
					}
				}else if(dom instanceof DomCDataSection) {
                    DomCDataSection domAttr = (DomCDataSection) dom;
                    String html = domAttr.getData();
                    Element element = Jsoup.parse(html);
                    String href = element.getElementsByTag("a").get(0).attr("href");
                    return PageParserHelper.getUrl(href, htmlPage);
                } else if(dom instanceof DomComment) {
                    DomComment domAttr = (DomComment) dom;
                    String html = domAttr.getData();
                    Element element = Jsoup.parse(html);
                    String href = element.getElementsByTag("a").get(0).attr("href");
                    return PageParserHelper.getUrl(href, htmlPage);

                }
			}
		}
		return null;
	}

	private String getLastCrawlerUrlXml(List<DomNode> domNodeList, ListDetail docList) {
		for (DomNode e : domNodeList) {
			DomCDataSection domAttr = (DomCDataSection) e;
			String html = domAttr.getData();
			Element element = Jsoup.parse(html);
			String href = element.getElementsByTag("a").get(0).attr("href");
			if (!href.contains("http://")) {
				href = docList.getDocHost().concat(href);
			}
			String name = element.getElementsByTag("a").get(0).text();
			OfficialDocument officialDocument = new OfficialDocument();
			officialDocument.setTitle(name);
			officialDocument.setUrl(href);
			if (Optional.ofNullable(officialDocument).isPresent()) {
				return officialDocument.getUrl();
			}
		}
		return null;
	}

	private Boolean isLastCrawlerList(List<WebElement> domNodeList, WebDriver htmlPage, String lastCrawlerUrl) {
		if (lastCrawlerUrl == null || domNodeList == null || domNodeList.size() == 0 || htmlPage == null) {
			return false;
		}
		for (WebElement dom : domNodeList) {
			if (dom != null) {
				String href = dom.getAttribute("href");
				if (href.contains(";jsessionid=")){
					href = href.replace(StringUtils.substringBetween(href,"initSnThreePageArticle.do","?"),"");
				}
				if (!StringUtils.isEmpty(href)) {
					if (StringUtils.equals(href, lastCrawlerUrl)) {
						return true;
					}
				}
			}
		}
		return false;
	}




	public  void parseTitleAndHref(HtmlPage  page){

	}

}
