package com.cetcbigdata.spider.work;

import org.springframework.stereotype.Component;

import com.cetcbigdata.spider.factory.WebDriverFactory;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author 作者
 * @version 创建时间：2018年9月21日 下午5:18:25 类说明
 */
@Component
public class WebJsClientPool extends WebClientPool {

	@Override
	public void run() {
		for (int i = 0; i < 30; i++) {
			WebClient webClient = WebDriverFactory.createJsClient(null);
			super.pool.add(webClient);
		}

	}

}
