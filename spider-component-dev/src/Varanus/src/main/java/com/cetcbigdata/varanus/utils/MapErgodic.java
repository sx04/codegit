package com.cetcbigdata.varanus.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunjunjie
 * @date 2020/8/20 13:28
 */
public class MapErgodic {

    //循环固有map
    public static StringBuilder MapDetail(HashMap<String, String> detailMap, StringBuilder detailsql) {
        for (Map.Entry<String, String> entry : detailMap.entrySet()) {
            String key = entry.getKey();
            String s = HumpToLine.humpToLine(key);
            String xpathValue = entry.getValue();
            detailsql.append(s + "=").append("\""+xpathValue + "\""+",");
        }
        return detailsql;
    }

    //循环动态map，并加Xpath
    public static StringBuilder MapDynamic(HashMap<String, String> dynamicMap, StringBuilder dynamicsql) {
        for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
            String key = entry.getKey();
            String s = HumpToLine.humpToLine(key);
            StringBuilder sb = new StringBuilder(s);
            String xpathName = sb.append("_xpath").toString();
            String xpathValue = entry.getValue();
            dynamicsql.append(xpathName + "=").append("\""+xpathValue +"\""+ ",");
        }
        return dynamicsql.deleteCharAt(dynamicsql.length()-1);
    }
}
