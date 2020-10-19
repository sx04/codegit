package com.cetcbigdata.varanus.parser;

import com.cetcbigdata.varanus.core.component.BaseRequestDocListParser;
import com.cetcbigdata.varanus.entity.*;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Created with IDEA author:Matthew Date:2019-3-21 Time:15:00
 * 
 * @author matthew
 */
public class JsonRequestDocListParser extends BaseRequestDocListParser {

	protected void parseDocList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo) throws Exception {

	}

	public void processList(ThreadLocal<WebClient> webClient, ListDetail docList, TaskBasicInfo taskBasicInfo) throws Exception {

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
