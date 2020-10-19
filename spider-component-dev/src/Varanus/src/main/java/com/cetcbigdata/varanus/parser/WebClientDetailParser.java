package com.cetcbigdata.varanus.parser;

import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.soap.Text;
import java.util.Map;
import com.cetcbigdata.varanus.core.component.BaseDocParser;


/**
 * @author sunjunjie
 * @date 2020/8/26 14:13
 */
@Component
public class WebClientDetailParser extends BaseDocParser {

    private OkHttpClient okHttpClient = new OkHttpClient();

    private static final Logger LOG = LoggerFactory.getLogger(WebClientDetailParser.class);

    //dynamicMap存动态Xpath  detailMap存固有值  detailData存爬取的数据
    public Map htmlPageParser(Map<String, String> dynamicMap, HtmlPage htmlPage, Map detailMap, Map detailData, String webName) {
        okHttpClient.dispatcher().setMaxRequests(128);
        okHttpClient.dispatcher().setMaxRequestsPerHost(16);
        String siteWidth = detailMap.get("siteWidth").toString();
        String sourceHtml = detailMap.get("sourceHtml").toString();
        //存储网站宽度
        if (!StringUtils.isEmpty(siteWidth)) {
            detailData.put("siteWidth", siteWidth);
        }
        //存储网站源码
        if (!StringUtils.isEmpty(sourceHtml)) {
            DomElement domNode = htmlPage.getFirstByXPath(sourceHtml);
            if(domNode!=null) {
                String textHtml = domNode.asXml();
                detailData.put("sourceHtml", textHtml);
            }
        }

        //循环遍历dynamicMap的Xpath的值并根据xpath爬取数据存储在map中返回
        for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
            String xpathKey = entry.getKey();
            String xpathValue = entry.getValue();
            if (!StringUtils.isEmpty(xpathValue)) {
                Object domNode = htmlPage.getFirstByXPath(xpathValue);
                if (domNode instanceof DomElement) {
                    if (domNode != null) {
                        DomElement d = (DomElement)domNode;
                        String href =((DomElement)domNode).getAttribute("href");
                        //判断是否为附件
                        if (!StringUtils.isEmpty(href)) {
                            // 处理附件
                            PageParserHelper.addAttachmentsHtml(htmlPage, xpathValue, xpathKey,
                                    Util.fileName(webName, attachmentsPath), detailData, okHttpClient);
                            //判断是否为图片
                        } else if ("img".equals(d.getTagName())) {
                            // 处理图片
                            PageParserHelper.addImgs(detailData, htmlPage, xpathValue, xpathKey,
                                    Util.fileName(webName, imgsPath), localImgsUrl, okHttpClient);
                        } else {
                            // 处理其他自定义Xpath
                            String data = d.asText();
                            if (!StringUtils.isBlank(data)) {
                                detailData.put(xpathKey, data);
                            }
                        }
                    }
                } else if(domNode instanceof Text){
                    detailData.put(xpathKey, domNode);
                }
            }
        }
        return detailData;
    }



    @Override
    protected OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument, ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy) throws Exception {
        return null;
    }

    @Override
    protected void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage, Response response) throws Exception {

    }
}
