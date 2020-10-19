package com.cetcbigdata.varanus.controller;

import com.cetcbigdata.varanus.common.WebDriverFactory;
import com.cetcbigdata.varanus.core.WebClientPool;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Created with IDEA author:Matthew Date:2019-3-26 Time:15:46
 */

@RestController
public class Test {

	@Autowired
	private WebDriverFactory webDriverFactory;
	Logger logger = LoggerFactory.getLogger(Test.class);

	@Autowired
	private WebClientPool webClientPool;
	@GetMapping("/driver")
	public void Test() {
		try {
			WebDriver webDriver = webDriverFactory.get();
			webDriver.get("https://www.baidu.com/");
			webDriver.findElement(By.id("kw")).sendKeys("你好");
			webDriver.findElement(By.id("su")).click();
			//webDriver.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@GetMapping("/proxy")
	public void proxy(){
		for (int i=0;i<10;i++){
			WebClient webClient =  webClientPool.getFromPool(true);
			logger.info("获取到的代理ip是 ->{}", webClient.getOptions().getProxyConfig().getProxyHost());
		}
	}
}
