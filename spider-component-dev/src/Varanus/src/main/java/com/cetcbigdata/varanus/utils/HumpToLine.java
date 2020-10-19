package com.cetcbigdata.varanus.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sunjunjie
 * @description: 驼峰转下划线/下划线转驼峰
 * @date 2020/8/12 13:38
 */
public class HumpToLine {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    }
