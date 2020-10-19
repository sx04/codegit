package com.cetcbigdata.varanus.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.BaseRequestDocListParser;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.*;
import com.gargoylesoftware.htmlunit.WebClient;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:15:00
 * 
 * @author matthew
 */
@Component
public class WebDriverRequestDocListParser extends BaseRequestDocListParser {
	private static final Logger LOG = LoggerFactory.getLogger(WebDriverRequestDocListParser.class);
	@Autowired
	private TaskWarningDAO taskWarningDAO;

	protected void parseDocList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo) throws Exception {

	}

	public void processList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo) throws Exception {

	}

	@Override
	public void processWebList(ThreadLocal<WebDriver> webDriverThreadLocal, ListDetail docList, TaskBasicInfo taskBasicInfo)
			throws Exception {
		Boolean o = checkNetWork.checkDocList();
		if (!o) {
			LOG.error("网络不通");
			/*TaskWarning taskWarning = new TaskWarning();
			taskWarning.setTaskId(taskBasicInfo.getTaskId());
			taskWarning.setListWarningType(Constants.NETWORK_WARNING);
			taskWarning.setListWarningDetail("网络不通");
			taskWarningDAO.save(taskWarning);*/
			throw new IOException();
		}
		parseWebDocList(webDriverThreadLocal, docList, taskBasicInfo);

	}

	/**
	 * 解析文档列表
	 *
	 * @param webClient
	 * @param configListEntity
	 * @param taskInfoEntity
	 * @param templateEntity
	 */
	@Override
	protected void parseDocList(WebClient webClient, ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity, TemplateEntity templateEntity) throws Exception {

	}

	@Override
	public void parseWebDocList(ThreadLocal<WebDriver> webDriverThreadLocal, ListDetail docList,
								TaskBasicInfo taskBasicInfo)throws Exception{
		String indexUrl = taskBasicInfo.getSectionUrl();
		String taskQueue = PageParserHelper.assembleQueue(docList.getTaskId(), docList.getListId());

		Integer startPage = 0;
		String bloomFilterKey = PageParserHelper.assebleBloomFilter(taskBasicInfo.getDepartment(),
				taskBasicInfo.getSectionTitle());

		getWebDriverList(webDriverThreadLocal, indexUrl, docList, taskQueue, startPage,
				redisBloomFilter, bloomFilterKey);

	}

	@Override
	public void processList(WebClient webClient, ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity, TemplateEntity templateEntity) throws Exception {

	}
}
