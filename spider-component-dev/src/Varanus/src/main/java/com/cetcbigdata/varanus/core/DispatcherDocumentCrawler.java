package com.cetcbigdata.varanus.core;

import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.DocDetailDAO;
import com.cetcbigdata.varanus.dao.DocIncrementDAO;
import com.cetcbigdata.varanus.dao.OfficialDocumentDAO;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.parser.WebClientDocParser;
import com.cetcbigdata.varanus.parser.WebDriverDocParser;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class DispatcherDocumentCrawler implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(DispatcherDocumentCrawler.class);

	private volatile RedisTemplate redisTemplate;
	private volatile WebClientPool webClientPool;
	private volatile WebDriverFactory webDriverFactory;
	private volatile WebClientDocParser webClientDocParser;
	private volatile WebDriverDocParser webDriverDocParser;
	private volatile OfficialDocumentDAO officialDocumentDAO;
	private volatile DocDetailDAO docDetailDAO;
	private volatile TaskWarningDAO taskWarningDAO;
	private volatile DocIncrementDAO docIncrementDAO;
	private volatile String contentHtmlSaveRootPath;

	ThreadLocal<WebClient> webClient = new ThreadLocal<>();
	OkHttpClient okHttpClient = new OkHttpClient();
	ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

	private volatile String taskQueue;
	private volatile List<DocDetail> docDetails;
	private volatile TaskBasicInfo taskBasicInfo;

	public DispatcherDocumentCrawler(RedisTemplate redisTemplate, WebClientPool webClientPool,
			WebDriverFactory webDriverFactory, WebClientDocParser webClientDocParser,
			WebDriverDocParser webDriverDocParser, OfficialDocumentDAO officialDocumentDAO, DocDetailDAO docDetailDAO,
			TaskWarningDAO taskWarningDAO, DocIncrementDAO docIncrementDAO, String taskQueue,
			List<DocDetail> docDetails, TaskBasicInfo taskBasicInfo,String contentHtmlSaveRootPath) {
		this.redisTemplate = redisTemplate;
		this.webClientPool = webClientPool;
		this.webClientDocParser = webClientDocParser;
		this.officialDocumentDAO = officialDocumentDAO;
		this.docDetailDAO = docDetailDAO;
		this.taskWarningDAO = taskWarningDAO;
		this.docIncrementDAO = docIncrementDAO;
		this.taskQueue = taskQueue;
		this.docDetails = docDetails;
		this.taskBasicInfo = taskBasicInfo;
		this.webDriverFactory = webDriverFactory;
		this.webDriverDocParser = webDriverDocParser;
		this.contentHtmlSaveRootPath=contentHtmlSaveRootPath;
	}

	@Override
	public void run() {
		String lockKey = "lock_" + taskQueue;
		try {
			parseDoc(taskQueue);
		} catch (Exception e) {
			LOG.error("this task parse error", e);
		} finally {
			// 释放锁
			LOG.info("本次任务key {} 执行完成,释放redis锁",taskQueue);
			redisTemplate.delete(lockKey);
		}
	}

	private  void parseDoc(String taskQueue) throws Exception {
		Integer increment = 0;
		String taskp = PageParserHelper.disassembleQueue(taskQueue);
		Integer taskId = Integer.valueOf(taskp.split(":")[0]);
		Integer listId = Integer.valueOf(taskp.split(":")[1]);
		Long  start = System.currentTimeMillis();
		while (redisTemplate.opsForList().size(taskQueue) > 0) {
			OfficialDocument officialDocument = (OfficialDocument) redisTemplate.opsForList().rightPop(taskQueue);
			Integer repeatCount = officialDocument.getRepeatCount();
			if (repeatCount == null) {
				repeatCount = 0;
			}
			URL url;
			try {
				url = new URL(officialDocument.getUrl());
			} catch (Exception e) {
				LOG.error(officialDocument.getUrl() + " is not a url" + e);
				continue;
			}
			try {
				officialDocument.setArea(taskBasicInfo.getArea());
				officialDocument.setDepartment(taskBasicInfo.getDepartment());
				officialDocument.setSectionTitle(taskBasicInfo.getSectionTitle());
				officialDocument.setTaskId(taskId);
				officialDocument.setListId(listId);
				officialDocument.setIsFile(false);
				if (StringUtils.isNoneBlank(taskBasicInfo.getDataSrcTypeName())) {
					officialDocument.setDataSrcTypeName(taskBasicInfo.getDataSrcTypeName());
				}
				// 一个任务可能有多套数据库模板配置，尝试使用不同模板进行匹配
				for (DocDetail docDetail : docDetails) {
					officialDocument.setDocId(docDetail.getDocId());
					docDetail = docDetailDAO.findDocDetailByDocId(docDetail.getDocId());
					if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(docDetail.getDocClientType())) {
						webClient.set(webClientPool.getFromPool(docDetail.getDocNeedProxy() == 1 ? true : false));
						WebRequest webRequest = new WebRequest(url, HttpMethod.GET);
						officialDocument = webClientDocParser.processDoc(docDetail, officialDocument, webClient,
								okHttpClient, webRequest, docDetail.getDocNeedProxy() == 1 ? true : false);
					}
					if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(docDetail.getDocClientType())) {
						webDriverThreadLocal.set(webDriverFactory.get());
						officialDocument = webDriverDocParser.processDoc(docDetail, officialDocument,
								webDriverThreadLocal, okHttpClient, docDetail.getDocNeedProxy() == 1 ? true : false);
					}
					if (officialDocument.getIsFile()) {
						officialDocumentDAO.save(officialDocument);
						increment++;
						break;
					} else if (Optional.ofNullable(officialDocument.getTextHtml()).isPresent()) {
						PageParserHelper.saveHtmlToFile(officialDocument,contentHtmlSaveRootPath);
						officialDocumentDAO.save(officialDocument);
						increment++;
						break;
					}
				}
				if (!Optional.ofNullable(officialDocument.getTextHtml()).isPresent() && !officialDocument.getIsFile()) {
					if (repeatCount < 3) {
						officialDocument.setRepeatCount(repeatCount + 1);
						redisTemplate.opsForList().leftPush(taskQueue, officialDocument);
					} else {
						/*TaskWarning taskWarning = new TaskWarning();
						taskWarning.setTaskId(taskId);
						taskWarning.setDocWarningType(Constants.TASK_TEMPLATE_WARNING);
						taskWarning.setDocWarningDetail(
								"任务doc模板，list_id:" + listId + "匹配文章" + officialDocument.getUrl() + "失败");
						taskWarningDAO.save(taskWarning);
						LOG.error("模板匹配失败 url为 {}",officialDocument.getUrl());*/
					}

				}
			} catch (Exception e) {
				LOG.error("模板匹配失败url--> {}",officialDocument.getUrl(),e);
				if (repeatCount < 3) {
					officialDocument.setRepeatCount(repeatCount + 1);
					LOG.warn("该任务 {} 被重新放入队列执行", JSONObject.toJSONString(officialDocument));
					redisTemplate.opsForList().leftPush(taskQueue, officialDocument);
				} else {
					/*TaskWarning taskWarning = new TaskWarning();
					taskWarning.setTaskId(taskId);
					taskWarning.setDocWarningType(Constants.TASK_TEMPLATE_WARNING);
					taskWarning.setDocWarningDetail("任务doc模板，list_id:" + listId + "未匹配文章" + officialDocument.getUrl());
					taskWarningDAO.save(taskWarning);*/

				}
			} finally {
				if (webDriverThreadLocal.get() != null) {
					webDriverFactory.close(webDriverThreadLocal.get());
				}
				if (webClient.get() != null) {
					webClient.get().close();
				}
				webDriverThreadLocal.remove();
				webClient.remove();
			}

		}
		Long end = System.currentTimeMillis();
		Long time = end -start;
		LOG.info("本次key {} 执行循环结束,总共耗时: {} 秒",taskQueue,time/1000);
		if (increment > 0) {
			DocIncrement docIncrement = new DocIncrement();
			docIncrement.setDepartment(taskBasicInfo.getDepartment());
			docIncrement.setSection(taskBasicInfo.getSectionTitle());
			docIncrement.setIncrement(increment);
			docIncrement.setTaskId(taskId);
			docIncrementDAO.save(docIncrement);
		}
	}

}
