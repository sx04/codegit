package com.cetcbigdata.varanus.common;

import com.gargoylesoftware.htmlunit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebDriverFactory {
	private static String chromeDriverPath;
	
	@Value("${chromeDriver.path}")
	private String chromeDriver;
	private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

	private int CAPACITY = 5;

	private AtomicInteger refCount = new AtomicInteger(0);

	/**
	 * store webDrivers available
	 */
	private BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>(
			CAPACITY);

	@PostConstruct
	public void getProperties() {
		chromeDriverPath = chromeDriver;
		this.CAPACITY = 5;
		innerQueue = new LinkedBlockingDeque<WebDriver>(5);
	}




	public static WebClient createClient(ProxyConfig proxy) {
		WebClient client = new WebClient(BrowserVersion.CHROME);
		WebClientOptions options = client.getOptions();
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setPrintContentOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setTimeout(10000);
		options.setPopupBlockerEnabled(false);
		options.setGeolocationEnabled(false);
		options.setJavaScriptEnabled(false);
		options.setDownloadImages(false);
		options.setActiveXNative(false);
		options.setAppletEnabled(false);
		options.setCssEnabled(false);
		options.setUseInsecureSSL(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		if (proxy != null) {
			options.setProxyConfig(proxy);
		}
		return client;
	}

	public static WebClient createJsClient(ProxyConfig proxy) {
		WebClient client = new WebClient(BrowserVersion.CHROME);
		WebClientOptions options = client.getOptions();
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setPrintContentOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(true);
		options.setTimeout(3000);
		options.setPopupBlockerEnabled(false);
		options.setGeolocationEnabled(false);
		options.setJavaScriptEnabled(true);
		options.setDownloadImages(false);
		options.setActiveXNative(false);
		options.setAppletEnabled(false);
		options.setCssEnabled(false);
		options.setUseInsecureSSL(true);
		if (proxy != null) {
			options.setProxyConfig(proxy);
		}
		return client;
	}


	
	/**
	 * 生产一个谷歌浏览器
	 * @param headless true表示不用打开浏览器界面，内存运行，false 表示需要打开浏览器界面。
	 * @return
	 */
	public  ChromeDriver getChromeDriver(boolean headless) {

		System.setProperty("webdriver.chrome.driver", chromeDriverPath);

		ChromeOptions option = new ChromeOptions();
		if (headless) {
			option.addArguments("--headless");
			option.addArguments("--disable-gpu");
			option.addArguments("--no-sandbox");
		}
		String [] dd = new String[]{"enable-automation"};
		option.setExperimentalOption("excludeSwitches", Arrays.asList(dd));
		return new ChromeDriver(option);
	}

	/**
	 * 生产一个火狐浏览器
	 * 
	 * @return
	 */

	public static FirefoxDriver getFirefoxDriver() {
		// 设置必要参数
		System.setProperty("webdriver.firefox.bin", "D:\\Program Files\\Mozilla Firefox\\firefox.exe");
		System.setProperty("webdriver.gecko.driver", "D:\\software\\selenium\\geckodriver.exe");
		return new FirefoxDriver();
	}


	public WebDriver get() throws InterruptedException {
		WebDriver poll = innerQueue.poll();
		if (poll != null) {
			return poll;
		}
		if (refCount.get() < CAPACITY) {
			synchronized (innerQueue) {
				if (refCount.get() < CAPACITY) {

					WebDriver mDriver = getChromeDriver(true);
					mDriver.manage().timeouts()
							.pageLoadTimeout(60, TimeUnit.SECONDS);
					innerQueue.add(mDriver);
					refCount.incrementAndGet();
				}
			}
		}
		return innerQueue.take();
	}

	public void returnToPool(WebDriver webDriver) {
		// webDriver.quit();
		// webDriver=null;
		innerQueue.add(webDriver);
	}

	public void close(WebDriver webDriver) {
		refCount.decrementAndGet();
		webDriver.quit();
		webDriver = null;
	}

	@PreDestroy
	public void shutdown() {
		try {
			for (WebDriver driver : innerQueue) {
				close(driver);
			}
			innerQueue.clear();
		} catch (Exception e) {
			LOG.warn("webdriverpool关闭失败",e);
		}
	}

}
