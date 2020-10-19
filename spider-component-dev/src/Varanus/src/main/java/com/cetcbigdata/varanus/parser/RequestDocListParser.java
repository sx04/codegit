package com.cetcbigdata.varanus.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.BaseRequestDocListParser;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.ConfigListEntity;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.entity.TaskWarningEntity;
import com.cetcbigdata.varanus.entity.TemplateEntity;
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
public class RequestDocListParser extends BaseRequestDocListParser {

	private static final Logger LOG = LoggerFactory.getLogger(RequestDocListParser.class);
	@Autowired
	private TaskWarningDAO taskWarningDAO;

	@Override
	public void processList(WebClient webClient, ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity, TemplateEntity templateEntity) throws Exception {
		Boolean o = checkNetWork.checkDocList();
		if (!o) {
			LOG.error("网络不通");
			/*TaskWarningEntity taskWarningEntity = new TaskWarningEntity();
			taskWarningEntity.setTemplateId(templateEntity.getId());
			taskWarningEntity.setListWarningType(Constants.NETWORK_WARNING);
			taskWarningEntity.setDetailWarningMsg("网络不通");
			taskWarningDAO.save(taskWarningEntity);*/
			throw new IOException();
		}
		parseDocList(webClient,configListEntity,taskInfoEntity,templateEntity);
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
		String indexUrl = taskInfoEntity.getSectionUrl();
		//以任务和模板ID作为redis队列Key
		String taskQueue = PageParserHelper.assembleQueue(taskInfoEntity.getId(), templateEntity.getId());

		Integer startPage = 0;
		String bloomFilterKey = PageParserHelper.assebleBloomFilter(taskInfoEntity.getWebName(),
				taskInfoEntity.getSectionTitle());

		if (Constants.DOC_RESPONSETYPE_HTML.equals(configListEntity.getListResponseType())) {
			getHtmlPageList(webClient, indexUrl, configListEntity, taskQueue, startPage, redisBloomFilter, bloomFilterKey);
		}
		if (Constants.DOC_RESPONSETYPE_JSON.equals(configListEntity.getListResponseType())
				|| Constants.DOC_RESPONSETYPE_XML.equals(configListEntity.getListResponseType())) {
			String jsonField = configListEntity.getListJsonField();
			String jsonId = configListEntity.getJsonIdKey();
			// 根据jsonfield的内容获取json返回中的哪些字段应该取回
			if (!StringUtils.isBlank(jsonField) || !StringUtils.isBlank(jsonId)) {
				// 通过json处理请求
				JSONObject jsonObject = JSON.parseObject(jsonField);
				//getHttpList(webClient, taskQueue, jsonObject, docList, redisBloomFilter, bloomFilterKey);
			}
		}
	}



	/*@Override
	protected void parseDocList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo)
			throws Exception {
		String indexUrl = taskBasicInfo.getSectionUrl();
		String taskQueue = PageParserHelper.assembleQueue(docList.getTaskId(), docList.getListId());

		Integer startPage = 0;
		String bloomFilterKey = PageParserHelper.assebleBloomFilter(taskBasicInfo.getDepartment(),
				taskBasicInfo.getSectionTitle());

		if (docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_HTML)) {
			getHtmlPageList(webClient, indexUrl, docList, taskQueue, startPage, redisBloomFilter, bloomFilterKey);
		}
		if (docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_JSON)|| docList.getListResponseType().equals(Constants.DOC_RESPONSETYPE_XML)) {
			String jsonField = docList.getListJsonField();
			String jsonId = docList.getJsonIdKey();
			// 根据jsonfield的内容获取json返回中的哪些字段应该取回
			if (!StringUtils.isBlank(jsonField) || !StringUtils.isBlank(jsonId)) {
				// 通过json处理请求
				JSONObject jsonObject = JSON.parseObject(jsonField);
				getHttpList(webClient, taskQueue, jsonObject, docList, redisBloomFilter, bloomFilterKey);
			}
		}
	}*/



}
