package com.cetcbigdata.varanus.core;

import com.cetcbigdata.varanus.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author matthew
 */
public abstract class BaseCrawlHandler {

	private static final Logger LOG = LoggerFactory.getLogger(BaseCrawlHandler.class);

	/**
	 * 开始执行流程
	 */
	abstract void process() throws Exception;

	/**
	 * 从数据库中拿取任务
	 */
	public Object takeTask(){
		return null;
	}


	/**
	 * 获取公文列表
	 */
	public void getDocList(ListDetail listDetail,TaskBasicInfo taskBasicInfo) throws Exception{

	}

	/**
	 * 获取公文列表
	 */
	public void getDocList(ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity,TemplateEntity templateEntity) throws Exception{

	}


	/**
	 * 组装参数
	 */
	public void buildParams(List<DocDetail> docDetails){

	}



	/**
	 * 保存数据
	 * @param officialDocument
	 * @param collectionName
	 */
	public void saveData(List<OfficialDocument> officialDocument, String collectionName){

	}


	/**
	 * 通知用户爬虫结束
	 */
	public void notifyCrawl(){

	}


}
