package com.cetcbigdata.spider.util;

import java.awt.Image;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

public class Util {

	public static boolean isFile(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		url = url.toLowerCase();
		if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls")
				|| url.endsWith(".xlsx") || url.endsWith(".wps") || url.endsWith(".rtf") || url.endsWith(".wpt")
				|| url.endsWith(".et") || url.endsWith(".ett")) {
			return true;
		} else {
			return false;
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
		String dateStr = "";
		String regx = "\\d{4}[-]\\d{1,2}[-]\\d{1,2}";
		Matcher m = Pattern.compile(regx).matcher(dataInfo);
		if (m.find()) {
			dateStr = m.group();
		} else {
			String regx1 = "\\d{4}[/]\\d{1,2}[/]\\d{1,2}";
			Matcher m1 = Pattern.compile(regx1).matcher(dataInfo);
			if (m1.find()) {
				dateStr = m1.group();
			} else {
				String regx2 = "\\d{4}[年]\\d{1,2}[月]\\d{1,2}[日]";
				Matcher m2 = Pattern.compile(regx2).matcher(dataInfo);
				if (m2.find()) {
					dateStr = m2.group();
				}
			}
		}
		return dateStr;
	}

}
