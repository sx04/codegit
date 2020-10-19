package com.cetcbigdata.varanus.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.dao.*;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.parser.RequestDocListParser;
import com.cetcbigdata.varanus.dao.ProjectinfoDAO;
import com.cetcbigdata.varanus.dao.TaskInfoDAO;
import com.cetcbigdata.varanus.entity.ProjectInfoEntity;
import com.cetcbigdata.varanus.parser.WebDriverRequestDocListParser;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cetcbigdata.varanus.parser.GetRequestDocListParser;
import com.cetcbigdata.varanus.parser.PostRequestDocListParser;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 爬虫抓取列表核心流程
 * 
 * @author matthew
 */
@Component
public class DispatcherListCrawler extends BaseCrawlHandler {

	@Autowired
	RedisTemplate redisTemplate;
	@Autowired
	WebClientPool webClientPool;
	@Autowired
	WebDriverFactory webDriverFactory;
	@Autowired
	GetRequestDocListParser getRequestDocListParser;
	@Autowired
	PostRequestDocListParser postRequestDocListParser;

	@Autowired
	private RequestDocListParser requestDocListParser;


	@Autowired
	WebDriverRequestDocListParser webDriverRequestDocListParser;
	@Autowired
	private ListDetailDAO listDetailDAO;
	@Autowired
	private TaskBasicInfoDAO taskBasicInfoDAO;

	@Autowired
	private TaskInfoDAO taskInfoDAO;
	@Autowired
	private ProjectinfoDAO projectinfoDAO;
	@Autowired
	private TemplateDAO templateDAO;

	ThreadLocal<WebClient> webClient = new ThreadLocal<>();
	ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

	private static final Logger LOG = LoggerFactory.getLogger(DispatcherListCrawler.class);

	DateFormat df = new SimpleDateFormat("HH:mm:ss");

	@Override
	public ListDetail takeTask() {
		/*
		 * ListDetail listDetail = new ListDetail(); listDetail.setListNeedProxy(false);
		 * listDetail.setListRequestType("GET"); listDetail.setListTemplateUrl(
		 * "http://kw.beijing.gov.cn/col/col19/index.html?uid=1435&pageNum=1");
		 * listDetail.setListResponseType("HTML");
		 * listDetail.setListXpath("//div[@class='default_pgContainer']//li/a");
		 */
		return null;
	}

	/*
	 * @Override void buildParams(Object o) {
	 * 
	 * }
	 */
	@Override
	public void getDocList(ListDetail listDetail, TaskBasicInfo taskBasicInfo) throws Exception {
		// 判断列表页对象是何种格式何种方法请求,从而选取不同的列表解析器
		try {
			if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(listDetail.getListClientType())) {
				webClient.set(webClientPool.getFromPool((listDetail.getListNeedProxy() == 1 ? true : false)));
				/*if (listDetail.getListRequestType().equals(HttpMethod.GET.name())) {
					LOG.info("任务实体 {},使用 GET 请求发起获取列表请求", JSON.toJSONString(taskBasicInfo));
					getRequestDocListParser.processList(webClient, listDetail, taskBasicInfo);
				} else if (listDetail.getListRequestType().equals(HttpMethod.POST.name())) {
					LOG.info("任务实体 {},使用 POST 请求发起获取列表请求", JSON.toJSONString(taskBasicInfo));
					postRequestDocListParser.processList(webClient, listDetail, taskBasicInfo);
				}*/



			}
			if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(listDetail.getListClientType())) {
				LOG.info("任务实体 {},使用 Webdriver 请求发起获取列表请求", JSON.toJSONString(taskBasicInfo));
				webDriverThreadLocal.set(webDriverFactory.get());
				webDriverRequestDocListParser.processWebList(webDriverThreadLocal, listDetail, taskBasicInfo);
			}
		} catch (Exception e) {
			throw e;
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

	@Override
	public void process() throws Exception {
		//查询所有的项目
		//List<TaskInfoEntity> taskInfoEntityList = taskInfoDAO.queryTaskInfo();
		List<ProjectInfoEntity> projectInfoEntities = projectinfoDAO.findByIsValid(1);
		//List<TaskBasicInfo> taskBasicInfos = taskBasicInfoDAO.findTasks();

		/*if (!CollectionUtils.isEmpty(taskBasicInfos)) {
				for (TaskBasicInfo taskBasicInfo : taskBasicInfos) {
					try {
						Integer taskId = taskBasicInfo.getTaskId();
						*//**
						 * 防止程序一开始获取到所有任务启动的，中途有需求需要停止的任务没有查询到
						 *//*
						Boolean taskStatus = taskBasicInfoDAO.findTaskBasicInfoByTaskId(taskId);
						if(taskStatus) {
							ListDetail listDetail = listDetailDAO.findByTaskId(taskId);
							LOG.info("查询到任务taskId {}, 所在部门{}，所在版块{},版块入口{}", taskBasicInfo.getTaskId(), taskBasicInfo.getDepartment(), taskBasicInfo.getSectionTitle(), taskBasicInfo.getSectionUrl());
							getDocList(listDetail, taskBasicInfo);
						}
					}catch (Exception e){
						LOG.error("执行任务taskId {}, 所在部门{}，所在版块{},版块入口{}出错 {}",taskBasicInfo.getTaskId(), taskBasicInfo.getDepartment(), taskBasicInfo.getSectionTitle(), taskBasicInfo.getSectionUrl(),e);
						continue;
					}
				}

		}
*/
		if (CollectionUtils.isNotEmpty(projectInfoEntities)){
			for(ProjectInfoEntity projectInfoEntity :projectInfoEntities){
				try {
					if (1 == projectInfoEntity.getIsRun()) {
						String time = df.format(new Date());
						if (time.equals(projectInfoEntity.getDispatchTime())) {
							//查询task表中所有的已配置的任务
							List<TaskInfoEntity> taskInfoEntityList = taskInfoDAO.findByState(2);
							for(TaskInfoEntity taskInfoEntity : taskInfoEntityList){
								//查询该任务下面对应的模板
								Integer taskId = taskInfoEntity.getId();
								List<TemplateEntity> templateEntities  = templateDAO.findByIsCorrectAndIsRunAndTaskId(1,1,taskId);
							}

						}
					}
				}catch (Exception e){

				}
			}
		}
	}

	@Override
	public void getDocList(ConfigListEntity configListEntity, TaskInfoEntity taskInfoEntity,TemplateEntity templateEntity) throws Exception {
		// 判断列表页对象是何种格式何种方法请求,从而选取不同的列表解析器
		try {
			if (Constants.REQUEST_CLIENT_TYPE_WEBCLIENT.equals(configListEntity.getListClientType())) {
				webClient.set(webClientPool.getFromPool((configListEntity.getListNeedProxy() == 1 ? true : false)));
				/*if (HttpMethod.GET.name().equals(configListEntity.getListRequestType())) {
					LOG.info("任务实体 {},使用 GET 请求发起获取列表请求", JSON.toJSONString(taskInfoEntity));
					getRequestDocListParser.processList(webClient.get(), configListEntity, taskInfoEntity,templateEntity);
				} else if (HttpMethod.POST.name().equals(configListEntity.getListRequestType())) {
					LOG.info("任务实体 {},使用 POST 请求发起获取列表请求", JSON.toJSONString(taskBasicInfo));
					postRequestDocListParser.processList(webClient, listDetail, taskBasicInfo);
				}*/
				//requestDocListParser.processList(webClient.get(), configListEntity, taskInfoEntity,templateEntity);


			}
			if (Constants.REQUEST_CLIENT_TYPE_WEBDRIVER.equals(configListEntity.getListClientType())) {
				LOG.info("任务实体 {},使用 Webdriver 请求发起获取列表请求", JSON.toJSONString(taskInfoEntity));
				webDriverThreadLocal.set(webDriverFactory.get());
				//webDriverRequestDocListParser.processWebList(webDriverThreadLocal, listDetail, taskBasicInfo);
			}
		} catch (Exception e) {
			throw e;
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

}
