package com.cetcbigdata.varanus.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-","");
	}

	/**
	 * 判断url是不是附件
	 * @param url
	 * @return
	 */
	public static boolean isFile(String url,OkHttpClient okHttpClient) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		if(checkEmailFormat(url)) {
			return false;
		}
		if (url.contains("javascript:")){
			return false;
		}

		Response response = null;
		try {
			Request request = new Request.Builder().url(url).build();
			url = url.toLowerCase();
			if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls")
					|| url.endsWith(".xlsx") || url.endsWith(".wps")) {
				return true;
				/**
				 * 判断是否是邮件地址，邮件地址不是附件，不需要下载
				 */
			}

			if (url.endsWith(".html")){
				return false;
			}

			else {
				if (url.startsWith("https")){
					response = okHttpClient.newBuilder().readTimeout
							(20, TimeUnit.SECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
							.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
							.build()
							.newCall(request)
							.execute();
				}
				else{
					response = okHttpClient.newCall(request).execute();
				}
				if (response.isSuccessful()) {
					String ContentDisposition = response.header("Content-Disposition");
					String ContentType = response.header("Content-Type");
					if (!StringUtils.isBlank(ContentType) || !StringUtils.isBlank(ContentDisposition)) {
						return isFileByResponseType(!StringUtils.isBlank(ContentType)?ContentType:ContentDisposition);
					}
				}
				return false;
			}
		} catch (Exception e) {
			LOG.error("判断是否是附件失败", e);
			return false;
		}finally {
			if (response!=null){
				response.close();
			}
		}
	}

	public static boolean isImage(URL url) {
		try {
			Image img = ImageIO.read(url);
			return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isFileByResponseType(String contentType) {
		if (StringUtils.isEmpty(contentType)) {
			return false;
		}
		contentType = contentType.toLowerCase();
		if (contentType.endsWith("pdf") || contentType.endsWith("msword") || contentType.endsWith("excel")
				|| contentType.endsWith("sheet") || contentType.endsWith("document") || contentType.endsWith("doc")
				|| contentType.endsWith("docx") || contentType.endsWith("xls") || contentType.endsWith("xlsx")
				|| contentType.endsWith("wps") || contentType.endsWith("rtf")) {
			return true;
		} else {
			return false;
		}
	}

	public static String delHtmlTags(String htmlStr) {
		// 定义script的正则表达式，去除js可以防止注入
		String scriptRegex = "<script[^>]*?>[\\s\\S]*?<\\/script>";
		// 定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
		String styleRegex = "<style[^>]*?>[\\s\\S]*?<\\/style>";
		// 定义HTML标签的正则表达式，去除标签，只提取文字内容
		String htmlRegex = "<[^>]+>";
		// 定义空格,回车,换行符,制表符
		String spaceRegex = "\\s*|\t|\r|\n";

		// 过滤script标签
		htmlStr = htmlStr.replaceAll(scriptRegex, "");
		// 过滤style标签
		htmlStr = htmlStr.replaceAll(styleRegex, "");
		// 过滤html标签
		htmlStr = htmlStr.replaceAll(htmlRegex, "");
		// 过滤空格等
		htmlStr = htmlStr.replaceAll(spaceRegex, "");
		return htmlStr.trim(); // 返回文本字符串
	}

	/**
	 * 判断是否包含HTML标签
	 * @param html
	 * @return
	 */
	public static boolean hasHtmlTags(String html){
		Pattern p = Pattern.compile("<(\\S*?)[^>]*>.*?| <.*? />");
		Matcher m = p.matcher(html);
		Boolean flag = false;
		// 找出所有html标记。
		while (m.find()) {
			flag = true;
			break;
		}
		return flag;
	}


	/**
	 * 获取HTML代码里的内容
	 * 
	 * @param htmlStr
	 * @return
	 */
	public static String getTextFromHtml(String htmlStr) {
		// 去除html标签
		htmlStr = delHtmlTags(htmlStr);
		// 去除空格" "
		htmlStr = htmlStr.replaceAll(" ", "");
		return htmlStr;
	}

	public static String getDateStrByRegx(String dataInfo) {
        if (StringUtils.isEmpty(dataInfo)) {
            return "";
        }
		String[] errMark = {  ";", "!",  "；", "!", "。", "￥","、","《","》","．" };
		for (String mark : errMark) {
			if (dataInfo.contains(mark)) {
				return "";
			}
		}
        String dateStr = "";
        String[] regxArray = {"((19|20)\\d{2})[-]\\d{1,2}[-]\\d{1,2}","((19|20)\\d{2})[-] \\d{1,2}[-] \\d{1,2}", "((19|20)\\d{2})[/]\\d{1,2}[/]\\d{1,2}",
                "((19|20)\\d{2})[.]\\d{1,2}[.]\\d{1,2}", "((19|20)\\d{2})[年]\\d{1,2}[月]\\d{1,2}[日]","((19|20)\\d{2})[年]\\d{1,2} [月]\\d{1,2}[日]",
                "[○|О|Ο|〇|Ｏ|0|零|o|O|一|二|三|四|五|六|七|八|九]{4}年[一|二|三|四|五|六|七|八|九|十]{1,2}月[一|二|三|四|五|六|七|八|九|十]{1,3}日",
				"((19|20)\\d{2})[-](0[1-9]|1[0-2])","((19|20)\\d{2})[/](0[1-9]|1[0-2])", "((19|20)\\d{2})[.](0[1-9]|1[0-2])",
				"((19|20)\\d{2})[年]\\d{1,2}[月]", "[○|О|Ο|〇|Ｏ|0|零|o|O|一|二|三|四|五|六|七|八|九]{4}年[一|二|三|四|五|六|七|八|九|十]{1,2}月"};
        for (int i = 0; i < regxArray.length; i++) {
            Matcher m = Pattern.compile(regxArray[i]).matcher(dataInfo);
            if (m.find()) {
                dateStr = m.group();
                break;
            }
        }
        return dateStr;
    }

	public static Map<String, Object> objectToMap(Object obj,Map<String, Object> map) throws Exception {
		if(obj == null) {
			return null;
		}
		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String key = property.getName();
			if (key.compareToIgnoreCase("class") == 0) {
				continue;
			}
			Method getter = property.getReadMethod();
			Object value = getter!=null ? getter.invoke(obj) : null;
			map.put(key, value);
		}

		return map;
	}

	/**
	 * 按月生成文件夹
	 * @param basePath
	 * @return
	 */
	public static String fileName(String department,String basePath){
		StringBuilder sb = new StringBuilder(basePath);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		String date = dateFormat.format(new Date());
		sb.append(CharacterPinYinConvertUtil.getPingYin(department)).append(File.separator).append(date).append(File.separator);
		return sb.toString();
	}

	/**
	 *检查Email 格式（正则表达式）
	 * @param content
	 * @return
	 */
	private static boolean checkEmailFormat(String content){
    /*
     * " \w"：匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'。
     * "|"  : 或的意思，就是二选一
     * "*" : 出现0次或者多次
     * "+" : 出现1次或者多次
     * "{n,m}" : 至少出现n个，最多出现m个
     * "$" : 以前面的字符结束
     */
		if (null==content || "".equals(content)){
			return true;
		}
		if (content.contains("mailto:")){
			return true;
		}
		if(content.contains("window.open")){
			return true;
		}
		if (content.contains(":window.print")){
			return true;
		}
		String regEx1 = "^@[a-z0-9]+(\\.[a-z]+)+";
		Pattern p = Pattern.compile(regEx1);
		Matcher m = p.matcher(content);
		if(m.matches()){
			return true;
		}else{
			return false;
		}
	}

	public static String getEncoding(String str) {
		String encode;
		encode = "UTF-16";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}
		encode = "ASCII";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return "字符串<< " + str + " >>中仅由数字和英文字母组成，无法识别其编码格式";
			}
		} catch (Exception ex) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}
		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(), encode))) {
				return encode;
			}
		} catch (Exception ex) {
		}

		return "未识别编码格式";
	}


	public static boolean isXmlDocument(String rtnMsg){

		boolean flag = true;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			builder.parse( new InputSource( new StringReader( rtnMsg )));
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}


	/**
	 * xml 转json
	 */
	public static String convertXmlToJson(String xml) throws IOException {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		return xmlJSONObj.toString();
	}

	public static void main(String[] args){
		System.out.println(getDateStrByRegx("\n" +
				"二О一一年八月十日"));
	}

	public static void writeStringToFile(String fileName,String text) throws IOException {
		ByteArrayInputStream in=null;
		FileOutputStream fos=null;
		FileChannel channel=null;
		try {
			in = new ByteArrayInputStream(text.getBytes());
			File contentFile = new File(fileName);
			File parentFile = contentFile.getParentFile();
			if (!parentFile.exists()){
				parentFile.mkdirs();
			}
			fos = new FileOutputStream(contentFile);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			channel = fos.getChannel();
			int num = 0;
			byte[] size = new byte[2048];
			while ((num = in.read(size)) != -1) {
				for (int i = 0; i < num; i++) {
					buffer.put(size[i]);
					buffer.flip(); // 此处必须要调用buffer的flip方法
					channel.write(buffer);
					buffer.clear();
				}
			}

		}catch (Exception e){
			throw new IOException(e);
		}finally {
			if (in!=null){
				try {
					in.close();
				}catch (Exception e){

				}
			}
			if (fos!=null){
				try {
					fos.close();
				}catch (Exception e){

				}
			}
			if (channel!=null){
				try {
					channel.close();
				}catch (Exception e){
				}

			}
		}

	}
}
