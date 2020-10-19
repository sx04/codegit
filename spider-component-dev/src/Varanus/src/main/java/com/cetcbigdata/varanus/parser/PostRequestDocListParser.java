package com.cetcbigdata.varanus.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.BaseRequestDocListParser;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.*;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.lang3.StringUtils;
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
public class PostRequestDocListParser extends BaseRequestDocListParser {

	private static final Logger LOG = LoggerFactory.getLogger(PostRequestDocListParser.class);
	@Autowired
	private TaskWarningDAO taskWarningDAO;

	public void processList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo)
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
		parseDocList(webClient, docList, taskBasicInfo);
	}

	protected void parseDocList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo)
			throws Exception {
		// TODO Auto-generated method stub
		String indexUrl = taskBasicInfo.getSectionUrl();
		String taskQueue = PageParserHelper.assembleQueue(docList.getTaskId(), docList.getListId());
		String bloomFilterKey = PageParserHelper.assebleBloomFilter(taskBasicInfo.getDepartment(),
				taskBasicInfo.getSectionTitle());

		if (docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_HTML)) {
			postHtmlPageList(webClient, indexUrl, docList, taskQueue, 1, redisBloomFilter, bloomFilterKey);
		}
		if (docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_XML)) {
			postHtmlPageList(webClient, indexUrl, docList, taskQueue, 1, redisBloomFilter, bloomFilterKey);
		}
		if (docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_JSON)) {
			String jsonField = docList.getListJsonField();
			String jsonId = docList.getJsonIdKey();
			// 根据jsonfield的内容获取json返回中的哪些字段应该取回
			if (!StringUtils.isBlank(jsonField) || !StringUtils.isBlank(jsonId)) {
				// 通过json处理请求
				JSONObject jsonObject = JSON.parseObject(jsonField);
				getHttpList(webClient, taskQueue, jsonObject, docList, redisBloomFilter, bloomFilterKey);
			}
		}
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
	public void processList(WebClient webClient, ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity, TemplateEntity templateEntity) throws Exception {

	}
}
