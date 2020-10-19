package com.cetcbigdata.varanus.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.CheckNetWork;
import com.cetcbigdata.varanus.entity.Proxy;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Component
public class WebClientPool implements Runnable {

	@Autowired
	private volatile RedisTemplate redisTemplate;
	@Autowired
	private ThreadPoolTaskExecutor  taskExecutor;

	protected volatile LinkedBlockingDeque<WebClient> pool = new LinkedBlockingDeque<>();

	private volatile LinkedBlockingDeque<WebClient> proxyPool = new LinkedBlockingDeque<>();

	private static final Logger LOG = LoggerFactory.getLogger(WebClientPool.class);

	OkHttpClient client = new OkHttpClient();

	private String PROXYURL = "http://http.tiqu.qingjuhe.cn/getip?num=1&type=2&pack=42761&port=1&ts=1&lb=1&pb=4&regions=";

	@PostConstruct
	public void initClientPool(){
		taskExecutor.execute(this);
	}


	public  WebClient getFromPool(Boolean proxy) {
		if (proxy) {
			LOG.info("use proxy");
			return getProxyClient();
		} else {
			return getNoProxyClient();
		}
	}

	private  WebClient getNoProxyClient() {
		try {
			if (pool.isEmpty()) {
				taskExecutor.execute(this);
			}
			WebClient webClient = pool.take();
			return webClient;
		} catch (InterruptedException e) {
			LOG.error("get webclient error", e);
		}
		return null;
	}

	private  WebClient getProxyClient() {
		try {
			WebClient webClient =null;
			if (proxyPool.isEmpty()) {
				taskExecutor.execute(new ProxyThread(proxyPool, redisTemplate));
			}
			LOG.info("get from proxyPool:{}", proxyPool.size());
			webClient = proxyPool.take();
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
		for (int i = 0; i < 100; i++) {
			WebClient webClient = WebDriverFactory.createClient(null);
			pool.add(webClient);
		}

	}

	private ProxyConfig createProxy(Proxy item) {
		return new ProxyConfig(item.getHost(), item.getPort(), false);
	}

	private void processResult(String json) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject object = JSON.parseObject(json);
			JSONArray jsonArray = object.getJSONArray("data");
			jsonArray.forEach((o) -> {
				JSONObject jo = (JSONObject) o;
				String host = jo.get("ip").toString();
				int port = Integer.parseInt(jo.get("port").toString());
				String expireTimeJo = jo.get("expire_time").toString();
				Proxy proxy = new Proxy();
				proxy.setHost(host);
				proxy.setPort(port);
				try {
					Date expireTime = df.parse(expireTimeJo);
					proxy.setExpireTime(expireTime);
				} catch (ParseException e) {
					LOG.error("转换过期时间异常", e);
				}
				redisTemplate.opsForList().rightPush(Constants.IPPOOL, proxy);
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
			LOG.info("proxy can use,proxyPool size->:{}", redisTemplate.opsForList().size(Constants.IPPOOL));
			Integer ipCount = redisTemplate.opsForList().size(Constants.IPPOOL).intValue();
			if (ipCount > 2) {
				for (int i = 0; i < ipCount; i++) {
					Proxy proxy = taskProxy();
					if (proxy != null) {
						Date expireTime = proxy.getExpireTime();
						Long start = expireTime.getTime();
						Long now =  System.currentTimeMillis();
						if (start.compareTo(now)>0 && CheckNetWork.pingHost(proxy.getHost(),proxy.getPort(),2000)){
							redisTemplate.opsForList().rightPush(Constants.IPPOOL,proxy);
						}
						WebClient webClient = WebDriverFactory.createClient(createProxy(proxy));
						proxyPool.add(webClient);
					}
				}
			} else {
				while (proxyPool.size()==0) {
					getProxy();
				}
			}
		}

		private  Proxy taskProxy(){
			Proxy proxy = (Proxy) redisTemplate.opsForList().leftPop(Constants.IPPOOL);
			if(Optional.ofNullable(proxy).isPresent() && !CheckNetWork.pingHost(proxy.getHost(),proxy.getPort(),2000)){
				proxy = (Proxy) redisTemplate.opsForList().leftPop(Constants.IPPOOL);
			}
			return proxy;
		}


		private   void getProxy() {
			Response response = null;
			try {
				Thread.sleep(5000);
				Request request = new Request.Builder().url(PROXYURL).build();
				response = client.newCall(request).execute();
				if (response.isSuccessful()) {
					byte[] res = response.body().bytes();
					String json = new String(res);
					processResult(json);
				}
			} catch (Exception e) {
				LOG.error("craw ip err", e);
			}finally {
				if (response!=null){
					response.close();
				}
			}
		}
	}

}
