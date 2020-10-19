package com.cetcbigdata.spider.work;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.cetcbigdata.spider.entity.OfficialDocument;
import com.cetcbigdata.spider.entity.OfficialDocumentList;
import com.cetcbigdata.spider.factory.RedisBloomFilter;

/**
 * @author matthew
 */
@Repository
public class SaveData {

	@Autowired
	private MongoTemplate mongoTemplatet;
	@Autowired
	private RedisBloomFilter redisBloomFilter;
	@Autowired
	public RedisTemplate redisTemplate;
	@Value("${crawler.task.collection}")
	private  String TMP_COLLECTION;

	private static final Logger LOG = LoggerFactory.getLogger(SaveData.class);

	@Async
	public void saveData(OfficialDocument officialDocument, String collectionName) {
		String url = officialDocument.getUrl();
		try {
			mongoTemplatet.save(officialDocument, collectionName);
			tmpData(officialDocument);
		} catch (DuplicateKeyException e) {
			redisBloomFilter.add(url);
			LOG.info("this url has been inserted {}", officialDocument.getUrl());
		}
	}

	@Async
	public void addQueue(String taskQueue, OfficialDocumentList officialDocumentList) {
		if (officialDocumentList != null) {
			if (officialDocumentList.getAgagin() != null && officialDocumentList.getAgagin()) {
				redisTemplate.opsForList().leftPush(taskQueue, officialDocumentList);
			} else {
				String url = officialDocumentList.getHref();
				if (redisBloomFilter.contains(url)) {
					LOG.info("this url has been crawled {}", officialDocumentList.getHref());
					return;
				} else {
					redisTemplate.opsForList().leftPush(taskQueue, officialDocumentList);
					redisBloomFilter.add(url);
				}
			}
		}
	}

	private void tmpData(OfficialDocument officialDocument){
		try {
			if (!mongoTemplatet.collectionExists(TMP_COLLECTION)) {
				mongoTemplatet.createCollection(TMP_COLLECTION);
				DBObject indexOptions = new BasicDBObject();
				indexOptions.put("url", 1);
				mongoTemplatet.getCollection(TMP_COLLECTION).createIndex(indexOptions, "url", true);
			}
			mongoTemplatet.save(officialDocument, TMP_COLLECTION);
		} catch (DuplicateKeyException e) {
			LOG.info("this url has been inserted into tmp {}", officialDocument.getUrl());
		}

	}

}
