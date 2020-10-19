package com.cetcbigdata.varanus.parser;


import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.core.component.BaseDocParser;
import com.cetcbigdata.varanus.core.component.PageParserHelperNew;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.entity.TableDetailConfigEntity;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author sunjunjie
 * @date 2020/8/26 14:23
 */
@Component
public class WebDriverDetailParser extends BaseDocParser {

    private OkHttpClient okHttpClient = new OkHttpClient();

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverDetailParser.class);


    @Override
    protected OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument, ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy) throws Exception {
        return null;
    }

    @Override
    public void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage, Response response)
            throws Exception {
    }

    //dynamicMap存动态Xpath  detailMap存固有值  detailData存爬取的数据
    public Map webDriverParser(Map<String, String> dynamicMap, Map detailMap, Map detailData, String webName,
                                   WebDriver webDriver) {
        okHttpClient.dispatcher().setMaxRequestsPerHost(16);
        okHttpClient.dispatcher().setMaxRequests(128);
        String siteWidth = detailMap.get("siteWidth").toString();
        String sourceHtml = detailMap.get("sourceHtml").toString();
        //存储网站宽度
        if (!StringUtils.isEmpty(siteWidth)) {
            detailData.put("siteWidth", siteWidth);
        }

        //存储网站源码
        if (!StringUtils.isEmpty(sourceHtml)) {
            WebElement domElement = webDriver.findElement(By.xpath(sourceHtml));
            if (domElement != null) {
                String textHtml = domElement.getAttribute("outerHTML");
                detailData.put("sourceHtml", textHtml);
            }
        }
        //循环遍历dynamicMap的Xpath的值并根据xpath爬取数据存储在map中返回
        for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
            String xpathKey = entry.getKey();
            String xpathValue = entry.getValue();
            if (!StringUtils.isEmpty(xpathValue)) {
                Object domElement = webDriver.findElement(By.xpath(xpathValue));
                if (domElement instanceof WebElement) {
                    if (domElement != null) {
                        // 处理附件
                        WebElement d = (WebElement)domElement;
                        String href = d.getAttribute("href");
                        //判断是否为附件
                        if (!StringUtils.isEmpty(href)) {
                            // 处理附件
                            PageParserHelperNew.addAttachmentsWebDriver(webDriver, xpathValue, xpathKey,
                                    Util.fileName(webName, attachmentsPath), detailData, okHttpClient);
                            //判断是否为图片
                        } else if ("img".equals(d.getTagName())) {
                            // 处理图片
                            PageParserHelperNew.addImgs(detailData, webDriver, xpathValue, xpathKey,
                                    Util.fileName(webName, imgsPath), localImgsUrl, okHttpClient);
                        } else {
                            // 处理其他自定义Xpath
                            String data = d.getText();
                            if (!StringUtils.isBlank(data)) {
                                detailData.put(xpathKey, data);
                            }
                        }
                    }
                } else if (domElement instanceof Text) {
                    detailData.put(xpathKey, domElement);
                }
            }
        }
        return detailData;
    }

    //TAB2验证表格类信息
    public Object verifyTable(TableDetailConfigEntity tableDetailConfigEntity,WebDriver webDriver) {
        WebElement table = webDriver.findElement(By.xpath(tableDetailConfigEntity.getTableXpath()));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        if (rows != null && !rows.isEmpty()) {
            List list1 = new ArrayList();
            for (WebElement row : rows) {
                List<WebElement> col = row.findElements(By.tagName("td"));
                if (col != null && !col.isEmpty()) {
                    List<String> list2 = new ArrayList<>();
                    for (WebElement cell : col) {
                        list2.add(cell.getText());
                    }
                    list1.add(list2);
                }
            }
            return list1;
        }
        return " ";
    }

}