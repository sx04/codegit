package com.cetcbigdata.spider.work;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cetcbigdata.spider.entity.OfficialDocumentList;
import com.cetcbigdata.spider.factory.SpringUtil;

@Component
public class CrawlerDispatcher implements Runnable {

	private Integer cpuCount = Runtime.getRuntime().availableProcessors();
	int threadNum = cpuCount * 2 + 1;// 根据cpu数量,计算出合理的线程并发数
	private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadNum);

	private volatile boolean isRunning = false;

	@Value("${crawler.task.count}")
	private String countTask;

	@Value("${crawler.task.queue}")
	private String TASK_QUEUE;

	private static final Logger LOG = LoggerFactory.getLogger(CrawlerDispatcher.class);

	@PostConstruct
	private void start() {
		for (int i=0;i<2;i++) {
			newFixedThreadPool.execute(this);
		}
	}
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (!isRunning) {
			Set<String> keySet = redisTemplate.keys(TASK_QUEUE);
			if (keySet != null && keySet.size() > 0) {
				for(String s:keySet) {
					redisTemplate.opsForValue().increment(countTask,1);
					try {
						crawlerWoker(s);
					} catch (Exception e) {
						LOG.error("this task process error", e);
					}
				}
			}
		}
	}

	public void crawlerWoker(String key) throws Exception {
		LOG.info("[begin] task_queue {}", key);
		if (redisTemplate.opsForList().size(key) > 0) {
			OfficialDocumentList officialDocumentList = (OfficialDocumentList) redisTemplate.opsForList().leftPop(key);
			if (officialDocumentList != null && officialDocumentList.getTaskClass() != null) {
				Integer count = officialDocumentList.getCrawlerCount();
				if (count < 4) {
					try {
						redisTemplate.opsForList().rightPush(key, officialDocumentList);
						Object clazz = SpringUtil.getBean(officialDocumentList.getTaskClass());
						ITask task = (ITask) clazz;
						task.process();
					} catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
						LOG.warn("no bean has been loaded", noSuchBeanDefinitionException);
					}
				} else {
					LOG.warn("the task {} has crawler too many times", officialDocumentList.getHref());
				}
			}
		}
	}

	@PreDestroy
	private void shuntdown() {
		newFixedThreadPool.shutdown();
	}

}
