package com.cetcbigdata.varanus.parser;

import com.cetcbigdata.varanus.constant.Constants;
import com.cetcbigdata.varanus.core.component.BaseDocParser;
import com.cetcbigdata.varanus.core.component.PageParserHelper;
import com.cetcbigdata.varanus.dao.TaskWarningDAO;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.entity.TaskWarning;
import com.cetcbigdata.varanus.utils.Constant;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-3-21
 * Time:15:00
 * @author matthew
 */
@Deprecated
@Component
public class WebDriverDocParser extends BaseDocParser {
    @Autowired
    private TaskWarningDAO taskWarningDAO;

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverDocParser.class);

    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument, ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy) throws Exception {
        return null;
    }

    @Override
    public OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument,
                                       ThreadLocal<WebDriver> webDriver, OkHttpClient okHttpClient, Boolean needProxy)
            throws Exception {
        buildBaseInfo(officialDocument);
        webDriver.get().get(officialDocument.getUrl());
        Thread.sleep(2000);
        if (webDriver.get().getPageSource() == null) {
            /*TaskWarning taskWarning = new TaskWarning();
            taskWarning.setTaskId(docDetail.getTaskId());
            taskWarning.setDocWarningType(Constants.URL_WARNING);
            taskWarning.setDocWarningDetail("url:" + officialDocument.getUrl() + "访问失败");
            taskWarningDAO.save(taskWarning);*/
            LOG.info("网址访问失败");
        }else if (Util.isFile(officialDocument.getUrl(),okHttpClient)){
            //地址为附件，直接进行附件下载
            saveAttachment(officialDocument,attachmentsPath);
        }
        else {
            webDriverParser(docDetail, officialDocument, webDriver.get());
        }
        return officialDocument;
    }

    @Override
    public void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage, Response response)
            throws Exception {
    }

    public  void webDriverParser(DocDetail docDetail, OfficialDocument officialDocument,
                                            WebDriver webDriver) {
        okHttpClient.dispatcher().setMaxRequestsPerHost(16);
        okHttpClient.dispatcher().setMaxRequests(128);
        // 获取正文部分的内容
        if (!StringUtils.isEmpty(docDetail.getContentXpath())) {
            WebElement domElement = webDriver.findElement(By.xpath(docDetail.getContentXpath()));

            // 处理附件
            /*if (!StringUtils.isEmpty(docDetail.getAttachmentXpath())) {
                PageParserHelper.addAttachments(officialDocument, webDriver, docDetail.getAttachmentXpath(),
                        Util.fileName(officialDocument.getDepartment(),attachmentsPath),localAttachmentsUrl,okHttpClient);
            }*/

            if (domElement != null) {
                // 处理图片
                if (!StringUtils.isEmpty(docDetail.getImageXpath())) {
                    PageParserHelper.addImgs(officialDocument, webDriver, docDetail.getImageXpath(),
                            Util.fileName(officialDocument.getDepartment(),imgsPath),localImgsUrl,okHttpClient);
                }
                String text = domElement.getText();
                officialDocument.setContent(text);
                String text_html = domElement.getAttribute("outerHTML");
                officialDocument.setTextHtml(text_html);
            } else {
                LOG.warn("no content to crawler");
                return;
            }
        }
        // 索引号
        if (!StringUtils.isEmpty(docDetail.getIndexNumberXpath())) {
            WebElement index_number_info_dom = webDriver.findElement(By.xpath(docDetail.getIndexNumberXpath()));
            if (index_number_info_dom != null) {
                String index_number_info = index_number_info_dom.getText();
                index_number_info = PageParserHelper.checkInfo(index_number_info);
                if (!StringUtils.isBlank(index_number_info)) {
                    officialDocument.setIndexNumberInfo(index_number_info);
                }
            }
        }
        // 主题分类
        if (!StringUtils.isEmpty(docDetail.getTopicTypeXpath())) {
            WebElement topic_cat_info_dom = webDriver.findElement(By.xpath(docDetail.getTopicTypeXpath()));
            if (topic_cat_info_dom != null) {
                String topic_cat_info = topic_cat_info_dom.getText();
                topic_cat_info = PageParserHelper.checkInfo(topic_cat_info);
                if (!StringUtils.isBlank(topic_cat_info)) {
                    officialDocument.setTopicCatInfo(topic_cat_info);
                }
            }
        }
        // 发文机关
        if (!StringUtils.isEmpty(docDetail.getPublishOfficeXpath())) {
            WebElement pub_office_info_dom = webDriver.findElement(By.xpath(docDetail.getPublishOfficeXpath()));
            if (pub_office_info_dom != null) {
                String pub_office_info = pub_office_info_dom.getText();
                pub_office_info = PageParserHelper.checkInfo(pub_office_info);
                if (!StringUtils.isBlank(pub_office_info)) {
                    officialDocument.setPubOfficeInfo(pub_office_info);
                    officialDocument.setCrawlerBoxPubOfficeInfo(pub_office_info);
                }
            }
        }
        // 成文日期
        if (!StringUtils.isEmpty(docDetail.getDraftDateXpath())) {
            WebElement draft_date_info_dom = webDriver.findElement(By.xpath(docDetail.getDraftDateXpath()));
            if (draft_date_info_dom != null) {
                String draft_date_info = draft_date_info_dom.getText();
                draft_date_info = PageParserHelper.checkInfo(draft_date_info);
                if (!StringUtils.isBlank(draft_date_info)) {
                    officialDocument.setDraftDateInfo(draft_date_info);
                }
            }
        }
        // 正文标题
        if (!StringUtils.isEmpty(docDetail.getTitleXpath())) {
            WebElement text_title_info_dom = webDriver.findElement(By.xpath(docDetail.getTitleXpath()));
            if (text_title_info_dom != null) {
                String text_title_info = text_title_info_dom.getText();
                if (!StringUtils.isBlank(text_title_info)) {
                    officialDocument.setTextTitleInfo(text_title_info);
                }
            }
        }

        // 标题
        if (!StringUtils.isEmpty(docDetail.getTitleXpath())) {
            WebElement text_title_info_dom = webDriver.findElement(By.xpath(docDetail.getTitleXpath()));
            if (text_title_info_dom != null) {
                String text_title_info = text_title_info_dom.getText();
                if (!StringUtils.isBlank(text_title_info)) {
                    officialDocument.setTitle(text_title_info);
                } else {
                    officialDocument.setIsNormal(Constant.SITE_NOT_NORMAL);
                }
            }
        }
        // 发文字号
        if (!StringUtils.isEmpty(docDetail.getReferenceNumberXpath())) {
            WebElement reference_number_info_dom = webDriver.findElement(By.xpath(docDetail.getReferenceNumberXpath()));
            if (reference_number_info_dom != null) {
                String reference_number_info = reference_number_info_dom.getText();
                if (!StringUtils.isBlank(reference_number_info)) {
                    officialDocument.setReferenceNumberInfo(reference_number_info);
                }
            }
        }
        // 发布日期
        if (!StringUtils.isEmpty(docDetail.getPublishDateXpath())) {
            WebElement pub_date_info_dom = webDriver.findElement(By.xpath(docDetail.getPublishDateXpath()));
            if (pub_date_info_dom != null) {
                String pub_date_info = pub_date_info_dom.getText();
                if (!StringUtils.isBlank(pub_date_info)) {
                    String pubDate = Util.getDateStrByRegx(pub_date_info);
                    if (!StringUtils.isBlank(pubDate)) {
                        officialDocument.setPubDateInfo(pubDate);
                    }

                }
            }
        }
        // 政策日期
        if (!StringUtils.isEmpty(docDetail.getPolicyDateXpath())) {
            PageParserHelper.getPolicyDate(webDriver, officialDocument, docDetail.getContentXpath(),
                    docDetail.getPolicyDateXpath());
        }
        //来源
        if (!StringUtils.isEmpty(docDetail.getSourceXpath())) {
            WebElement source_dom = webDriver.findElement(By.xpath(docDetail.getSourceXpath()));
            if (source_dom != null) {
                String source = source_dom.getText();
                if (!StringUtils.isBlank(source)) {
                    if (StringUtils.isNoneBlank(officialDocument.getPubDateInfo())){
                        if (source.contains(officialDocument.getPubDateInfo())){
                            source = StringUtils.replace(source,officialDocument.getPubDateInfo(),"");
                            if (source.contains("发布日期")){
                                source = source.replace("发布日期","");
                            }
                        }
                    }
                    source = PageParserHelper.checkInfo(source);
                    if (!StringUtils.isBlank(source)) {
                        officialDocument.setSource(source);
                    }
                }
            }
        }

        // 主题词
        if (!StringUtils.isEmpty(docDetail.getTopicWordsXpath())) {
            WebElement topic_words_info_dom = webDriver.findElement(By.xpath(docDetail.getTopicWordsXpath()));
            if (topic_words_info_dom != null) {
                String topic_words_info = topic_words_info_dom.getText();
                topic_words_info = PageParserHelper.checkInfo(topic_words_info);
                if (!StringUtils.isBlank(topic_words_info)) {
                    officialDocument.setTopicWordsInfo(topic_words_info);
                }
            }
        }
        // 网站宽度
        if (Optional.ofNullable(docDetail.getSiteWidth()).isPresent()) {
            officialDocument.setSiteWidth(docDetail.getSiteWidth());
        }
    }
}
