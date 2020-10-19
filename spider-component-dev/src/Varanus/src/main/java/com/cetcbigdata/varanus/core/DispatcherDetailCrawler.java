package com.cetcbigdata.varanus.core;

import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.*;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.entity.TaskBasicInfo;
import com.cetcbigdata.varanus.parser.JsonDocParser;
import com.cetcbigdata.varanus.parser.WebClientDocParser;
import com.cetcbigdata.varanus.parser.WebDriverDocParser;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫抓取详情核心流程
 * 
 * @author matthew
 */
@Component
public class DispatcherDetailCrawler extends BaseCrawlHandler implements Runnable {

	@Autowired
	volatile RedisTemplate redisTemplate;
	@Autowired
	volatile WebClientPool webClientPool;
	@Autowired
	WebDriverFactory webDriverFactory;
	@Autowired
	WebClientDocParser webClientDocParser;
	@Autowired
	WebDriverDocParser webDriverDocParser;
	@Autowired
	JsonDocParser jsonDocParser;
	@Autowired
	private TaskBasicInfoDAO taskBasicInfoDAO;
	@Autowired
	private DocDetailDAO docDetailDAO;
	@Autowired
	private TaskWarningDAO taskWarningDAO;
	@Autowired
	private DocIncrementDAO docIncrementDAO;
	@Autowired
	private OfficialDocumentDAO officialDocumentDAO;
	@Autowired
	private TaskExecutor taskExecutor;

	@Value("${varanus.file.contentHtmlPath}")
	private String contentHtmlSaveRootPath;

	private volatile boolean isRunning = false;


	private static final Logger LOG = LoggerFactory.getLogger(DispatcherDetailCrawler.class);

	@PostConstruct
	private void start() {
		// for (int i = 0; i < 2; i++) {
		taskExecutor.execute(this);
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (!isRunning) {
			Set<String> keySet = redisTemplate.keys(Constants.VARANUS_TASK_PAT);
			if (keySet != null && keySet.size() > 0) {
				for (String s : keySet) {
					try {
						crawlerWorker(s);
					} catch (Exception e) {
						LOG.error("this task process error", e);
					}
				}
			}
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				LOG.error("休眠线程中断", e);
			}
		}
	}

	@Override
	public void process() throws Exception {

	}

	@Override
	public Object takeTask() {
		// 从redis队列中拿到list列表

		// 联表查询数据库拿出List对应的detail中的任务
		// 同一个list任务对应的队列中采用的解析模板一致
		return null;
	}

	/*
	 * @Override void buildParams(List<DocDetail> docDetails,TaskBasicInfo
	 * taskBasicInfom,OfficialDocument officialDocument) {
	 *
	 *
	 * }
	 */

	@Override
	public void saveData(List<OfficialDocument> officialDocument, String collectionName) {

	}

	/**
	 * 通知用户爬虫结束
	 */
	@Override
	public void notifyCrawl() {

	}

	private void crawlerWorker(String s) throws Exception {

		if (redisTemplate.opsForList().size(s) > 0) {
			Boolean locked = false;
			String lockKey = "lock_" + s;
			Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, 1);
			if (success != null && success) {
				redisTemplate.expire(lockKey, 15, TimeUnit.MINUTES);
				locked = true;
			}
			if (locked) {
				String taskp = PageParserHelper.disassembleQueue(s);
				Integer taskId = Integer.valueOf(taskp.split(":")[0]);
				Integer listId = Integer.valueOf(taskp.split(":")[1]);
				TaskBasicInfo taskBasicInfo = taskBasicInfoDAO.findByTaskId(taskId);
				List<DocDetail> docDetails = docDetailDAO.findByListId(listId);
				if (Optional.ofNullable(taskBasicInfo).isPresent() && CollectionUtils.isNotEmpty(docDetails)) {
					DispatcherDocumentCrawler dispatcherDocumentCrawler =
							new DispatcherDocumentCrawler(redisTemplate, webClientPool, webDriverFactory, webClientDocParser, webDriverDocParser,
									officialDocumentDAO, docDetailDAO, taskWarningDAO, docIncrementDAO, s, docDetails, taskBasicInfo, contentHtmlSaveRootPath);
					taskExecutor.execute(dispatcherDocumentCrawler);
				}
			}
		}
	}

}