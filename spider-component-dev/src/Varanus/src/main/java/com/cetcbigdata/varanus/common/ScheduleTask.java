package com.cetcbigdata.varanus.common;

import com.cetcbigdata.varanus.core.DispatcherListCrawler;
import com.cetcbigdata.varanus.dao.DocIncrementDAO;
import com.cetcbigdata.varanus.utils.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 读取配置server.cron触发定时任务 author:宋旻雨 Date:2019-4-2 Time:11:23
 */

@Component
public class ScheduleTask {

	@Autowired
	private DispatcherListCrawler dispatcherListCrawler;
	@Autowired
	private DocIncrementDAO docIncrementDAO;
	@Autowired
	private MailUtil mailUtil;
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleTask.class);

	@Value("${varanus.mail.to}")
	private String toMail;
	@Value("${varanus.warning.url}")
	private String warningUrl;

	//@Scheduled(cron = "${server.cron}") // 添加定时任务r;
	private void configureTasks() {
		LOG.info("开始执行定时任务时间: {}" , LocalDateTime.now());
		Long  start = System.currentTimeMillis();
            try {
				dispatcherListCrawler.process();
            } catch (Exception e) {
               LOG.error("获取列表信息出错",e);
            }
		Long end = System.currentTimeMillis();
        Long time = end -start;
		LOG.info("总共耗时: {} 秒" , time/1000 );
	}

	@Scheduled(cron = "0 0 21 * * ?")
	private void scheduleSendEmail() {

		Object[] incrementObjects = docIncrementDAO.findIncrementGroupByDeparnment();
		if (incrementObjects.length > 0) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String messageTime = sdf.format(date);

			StringBuffer mailMsg = new StringBuffer();
			mailMsg.append("<h3>爬虫" + messageTime + "统计结果</h3><table border=\"1\" width=\"800px\" height=\"200px\" >");
			mailMsg.append("<tr><th>部门</th><td>板块</td><td>新增</td></tr>");
			for (Object object : incrementObjects) {
				Object[] increment = (Object[]) object;
				String department = (String) increment[0];
				String section = (String) increment[1];
				BigDecimal count = (BigDecimal) increment[2];
				mailMsg.append("<tr><th>" + department + "</th><td>" + section + "</td><td>" + count + "</td></tr>");
			}
			mailMsg.append("</table>");
			// String warningUrl =
			// "http://localhost:1024/index.html#/Warnings/WarningOverviewList";
			mailMsg.append("<div><p style=\"line-height:2;\"></p><p align=\"center\">报警详情请访问<a href=\"" + warningUrl
					+ "\">Varanus监控页面</a></p></div>");
			// String toMail =
			// "diaofeng@cetcbigdata.com;songminyu@cetcbigdata.com;maxinfan@cetcbigdata.com";
			mailUtil.sendMail(toMail, "爬虫统计结果" + messageTime, mailMsg.toString());
		}
	}
}
