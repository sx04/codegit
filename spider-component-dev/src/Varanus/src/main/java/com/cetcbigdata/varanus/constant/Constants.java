package com.cetcbigdata.varanus.constant;

/**
 * Created with IDEA author:Matthew Date:2018-12-26 Time:14:50
 */
public class Constants {

	public static final String VARANUS_TASK = "varanus:task:";

	public static final String VARANUS_TASK_PAT = "varanus:task:*";

	public static final String VARANUS_LAST_URL = "varanus:last:url:";

	public static final String VARANUS_BLOOM_KEY = "varanus:bloom:filter:";

	public static final Integer NETWORK_WARNING = 0;

	public static final Integer URL_WARNING = 1;

	public static final Integer TASK_TEMPLATE_WARNING = 2;

	public static final String NETWORK_WARNING_REASON = "网络异常";

	public static final String URL_WARNING_REASON = "网址访问失败";

	public static final String TASK_TEMPLATE_WARNING_REASON = "模板匹配失败";

	public static final String DOC_RESPONSETYPE_HTML = "HTML";

	public static final String DOC_RESPONSETYPE_JSON = "JSON";

	public static final String DOC_RESPONSETYPE_XML = "XML";

	public static final String REQUEST_CLIENT_TYPE_WEBCLIENT = "WEBCLIENT";

	public static final String REQUEST_CLIENT_TYPE_WEBDRIVER = "WEBDRIVER"; //爬虫请求工具使用模拟浏览器

	public static final String REQUEST_CLIENT_TYPE_HTTP = "HTTP";

	public static final String IPPOOL = "ip:pool";

}
