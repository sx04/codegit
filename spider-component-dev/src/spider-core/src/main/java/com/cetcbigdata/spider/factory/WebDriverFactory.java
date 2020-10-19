package com.cetcbigdata.spider.factory;

import javax.annotation.PostConstruct;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;

@Component
public class WebDriverFactory {
	private static String chromeDriverPath;
	
	@Value("${chromeDriver.path}")
	private String chromeDriver;
	
	@PostConstruct
	public void getProperties() {
		this.chromeDriverPath = chromeDriver;
	}

	/**
	 * 生成一个模拟浏览器
	 * 
	 * @return
	 */
	public static PhantomJSDriver getPhantomJSDriver() {
		// 设置必要参数
		DesiredCapabilities dcaps = new DesiredCapabilities();
		// ssl证书支持
		dcaps.setCapability("acceptSslCerts", true);
		// 截屏支持
		dcaps.setCapability("takesScreenshot", false);
		// css搜索支持
		dcaps.setCapability("cssSelectorsEnabled", true);
		dcaps.setCapability("loadImages", false);
		// js支持
		dcaps.setJavascriptEnabled(true);

		// 驱动支持
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"D:\\software\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
		PhantomJSDriver driver = new PhantomJSDriver(dcaps);
		return driver;
	}

	public static WebClient createClient(ProxyConfig proxy) {
		WebClient client = new WebClient(BrowserVersion.CHROME);
		WebClientOptions options = client.getOptions();
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setPrintContentOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setTimeout(3000);
		options.setPopupBlockerEnabled(false);
		options.setGeolocationEnabled(false);
		options.setJavaScriptEnabled(false);
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
	 * 生成一个带请求头的模拟浏览器
	 * 
	 * @return
	 */
	public static PhantomJSDriver getPhantomJSDriverWithHeader() {
		// 设置必要参数
		DesiredCapabilities dcaps = new DesiredCapabilities();
		// ssl证书支持
		dcaps.setCapability("acceptSslCerts", true);
		// 截屏支持
		dcaps.setCapability("takesScreenshot", false);
		// css搜索支持
		dcaps.setCapability("cssSelectorsEnabled", true);

		dcaps.setCapability("loadImages", false);

		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";

		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
		// js支持
		dcaps.setJavascriptEnabled(true);
		// 驱动支持
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"D:\\software\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
		PhantomJSDriver driver = new PhantomJSDriver(dcaps);

		return driver;
	}

	
	/**
	 * 生产一个谷歌浏览器
	 * @param headless true表示不用打开浏览器界面，内存运行，false 表示需要打开浏览器界面。
	 * @return
	 */
	public static ChromeDriver getChromeDriver(boolean headless) {

		System.setProperty("webdriver.chrome.driver", chromeDriverPath);

		ChromeOptions option = new ChromeOptions();
		if (headless) {
			option.addArguments("--headless");
			option.addArguments("--disable-gpu");
		}

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

}
