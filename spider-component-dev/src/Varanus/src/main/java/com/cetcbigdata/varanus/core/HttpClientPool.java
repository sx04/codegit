package com.cetcbigdata.varanus.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import okhttp3.OkHttpClient;

@Component
public class HttpClientPool implements Runnable {

	private Integer threadCount = Runtime.getRuntime().availableProcessors();
	private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadCount);

	private LinkedBlockingDeque<OkHttpClient> pool = new LinkedBlockingDeque<>();

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientPool.class);

	@PreDestroy
	private void shuntdown() {
		newFixedThreadPool.shutdown();
	}

	@Override
	public void run() {

		for (int i = 0; i < 10; i++) {
			OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
			pool.add(okHttpClient);
		}

	}

	public OkHttpClient getFromPool() {
		try {
			if (pool.isEmpty()) {
				newFixedThreadPool.execute(this);
			}
			OkHttpClient okHttpClient = pool.take();
			return okHttpClient;
		} catch (InterruptedException e) {
			LOG.error("get webclient error", e);
		}
		return null;
	}

}
