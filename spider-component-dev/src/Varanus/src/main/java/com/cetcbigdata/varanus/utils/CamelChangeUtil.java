package com.cetcbigdata.varanus.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: service-zhiwen-es
 * @description: 数据库下划线字段转驼峰格式
 * @author: Robin
 * @create: 2018-11-26 16:02
 **/
public class CamelChangeUtil {
    public static final String UNDERLINE = "_";
    /**
     * 下划线格式字符串转换为驼峰格式字符串
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile(UNDERLINE).matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

    public static void main(String [] args){
        String url = "http://www.sninfo.gov.cn:8083/initSnThreePageArticle.do;jsessionid=08E5BAE094D90657F77F6CAE1469BAD0?method=initSnThreePageArticle&articleTypeId=20549&articleId=149272";
        if (url.contains(";jsessionid=")){
            System.out.println(url.replace(StringUtils.substringBetween(url,"initSnThreePageArticle.do","?"),""));
        }
    }
}
