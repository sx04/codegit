/*
package com.cetcbigdata.varanus.core;

import com.cetcbigdata.varanus.common.RedisBloomFilter;
import com.cetcbigdata.varanus.entity.ListDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

*/
/**
 * @author matthew
 *//*

@Repository
public class SaveData {

	@Autowired
	private RedisBloomFilter redisBloomFilter;
	@Autowired
	public RedisTemplate redisTemplate;

	private static final Logger LOG = LoggerFactory.getLogger(SaveData.class);

	@Async
	public void saveData(OfficialDocument officialDocument, String collectionName) {
		String url = officialDocument.getUrl();
		try {
			//mongoTemplatet.save(officialDocument, collectionName);
			//tmpData(officialDocument);
		} catch (DuplicateKeyException e) {
			redisBloomFilter.add(url);
			LOG.info("this url has been inserted {}", officialDocument.getUrl());
		}
	}

	@Async
	public void addQueue(String taskQueue, ListDetail listDetail) {
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
			mongoTemplatet.save(officialDocument, TMP_COLLECTION);
		} catch (DuplicateKeyException e) {
			LOG.info("this url has been inserted into tmp {}", officialDocument.getUrl());
		}

	}

}
*/
