package com.cetcbigdata.spider.work;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.spider.entity.Proxy;
import com.cetcbigdata.spider.factory.WebDriverFactory;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class WebClientPool implements Runnable {

	@Autowired
	private RedisTemplate redisTemplate;

	private Integer threadCount = Runtime.getRuntime().availableProcessors();
	private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(threadCount);

	protected volatile LinkedBlockingDeque<WebClient> pool = new LinkedBlockingDeque<>();

	private volatile LinkedBlockingDeque<WebClient> proxyPool = new LinkedBlockingDeque<>();

	private static final Logger LOG = LoggerFactory.getLogger(WebClientPool.class);

	private static final int CONNTIMEOUT = 2 * 1000;

	OkHttpClient client = new OkHttpClient();

	private String PROXYURL = "http://webapi.http.zhimacangku.com/getip?num=8&type=2&pro=&city=0&yys=0&port=1&pack=37480&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=";

	@PreDestroy
	private void shuntdown() {
		newFixedThreadPool.shutdown();
	}

	public synchronized WebClient getFromPool(Boolean proxy) {
		if (proxy) {
			LOG.info("use proxy");
			return getProxyClient();
		} else {
			return getNoProxyClient();
		}
	}

	private synchronized WebClient getNoProxyClient() {
		try {
			if (pool.isEmpty()) {
				newFixedThreadPool.execute(this);
			}
			WebClient webClient = pool.take();
			return webClient;
		} catch (InterruptedException e) {
			LOG.error("get webclient error", e);
		}
		return null;
	}

	private synchronized WebClient getProxyClient() {
		try {
			if (proxyPool.isEmpty()) {
				newFixedThreadPool.execute(new ProxyThread(proxyPool, redisTemplate));
			}
			LOG.info("get from proxyPool:{}", proxyPool.size());
			WebClient webClient = proxyPool.take();
			return webClient;
		} catch (InterruptedException e) {
			LOG.error("get webclient error", e);
		}
		return null;
	}

	public boolean returnToPool(WebClient webClient) {
		try {
			proxyPool.put(webClient);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < 30; i++) {
			WebClient webClient = WebDriverFactory.createClient(null);
			pool.add(webClient);
		}

	}

	private ProxyConfig createProxy(Proxy item) {
		return new ProxyConfig(item.getHost(), item.getPort(), false);
	}

	private void processResult(String json) {
		try {
			JSONObject object = JSON.parseObject(json);
			JSONArray jsonArray = object.getJSONArray("data");
			jsonArray.forEach((o) -> {
				JSONObject jo = (JSONObject) o;
				String host = jo.get("ip").toString();
				int port = Integer.parseInt(jo.get("port").toString());
				Proxy proxy = new Proxy();
				proxy.setHost(host);
				proxy.setPort(port);
				redisTemplate.opsForList().rightPush("ippool", proxy);
				WebClient webClient = WebDriverFactory.createClient(createProxy(proxy));
				proxyPool.add(webClient);
			});
		} catch (Exception e) {
			LOG.error("parse row err", e);
		}
	}

	class ProxyThread implements Runnable {

		private volatile LinkedBlockingDeque<WebClient> proxyPool = new LinkedBlockingDeque<>();
		private volatile RedisTemplate redisTemplate;

		public ProxyThread(LinkedBlockingDeque<WebClient> proxyPool, RedisTemplate redisTemplate) {
			this.proxyPool = proxyPool;
			this.redisTemplate = redisTemplate;
		}

		@Override
		public void run() {
			LOG.info("proxy can use,proxyPool size->:{}", redisTemplate.opsForList().size("ippool"));
			Integer ipCount = redisTemplate.opsForList().size("ippool").intValue();
			if (ipCount > 2) {
				for (int i = 0; i < ipCount; i++) {
					Object o = redisTemplate.opsForList().leftPop("ippool");
					if (o != null) {
						Proxy proxy = (Proxy) o;
						WebClient webClient = WebDriverFactory.createClient(createProxy(proxy));
						proxyPool.add(webClient);
					}
				}
			} else {
				getProxy();
			}
		}

		private synchronized void getProxy() {
			try {
				Thread.sleep(2000);
				Request request = new Request.Builder().url(PROXYURL).build();
				Response response = null;
				response = client.newCall(request).execute();
				if (response.isSuccessful()) {
					byte[] res = response.body().bytes();
					String json = new String(res);
					processResult(json);
				}
			} catch (Exception e) {
				LOG.error("craw ip err", e);
			}
		}
	}

}
