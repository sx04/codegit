package com.cetcbigdata.varanus.core.component;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.entity.Attachment;
import com.cetcbigdata.varanus.entity.Img;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.cetcbigdata.varanus.utils.SSLSocketClient;
import com.cetcbigdata.varanus.utils.Util;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sunjunjie
 * @date 2020/8/26 15:01
 */

public class PageParserHelperNew {

    private static final Logger LOG = LoggerFactory.getLogger(PageParserHelperNew.class);


    //webClient的附件采集
    public static  void addAttachmentsHtml(HtmlPage htmlPage,String xpathValue, String xpathKey, String basePath,HashMap detailData, OkHttpClient okHttpClient) {
        List<DomElement> attachment = htmlPage.getByXPath(xpathValue);
        if (CollectionUtils.isNotEmpty(attachment)) {
            List<Attachment> attachments = new ArrayList<>();
            for(DomElement e:attachment) {
                try {
                    String href = e.getAttribute("href");
                    LOG.info("附件的原始路径是->{}", href);
                    String name = e.getTextContent();
                    String hrefNew = getUrl(href, htmlPage);
                    if (Util.isFile(hrefNew, okHttpClient)) {
                        Attachment atta = new Attachment();
                        atta.setAttachmentsStatus(0);
                        LOG.info("获取到的绝对路径是->{}", hrefNew);
                        atta.setUrl(hrefNew);
                        atta.setTitle(name);
                        String filePath = download(hrefNew, atta, basePath, okHttpClient);
                        if (atta.getMyUrl() != null) {
                            atta.setMyUrl(hrefNew);
                        } else {
                            atta.setMyUrl(filePath);
                            if (!StringUtils.isEmpty(filePath)) {
                                //String localUrl = filePath.replace(basePath,localAttachmentsUrl);
                                e.setAttribute("href", filePath);
                            }
                        }
                        attachments.add(atta);
                    } else {
                        LOG.warn("{} unknown attachment file ", hrefNew);
                    }
                }catch (Exception ee){
                    LOG.warn("下载附件失败 {}",htmlPage.getUrl(),ee);
                }
            }
            if (!CollectionUtils.isEmpty(attachments)) {
                detailData.put(xpathKey,JSON.toJSONString(attachments));
            }
        }
    }

    //webDriver的附件采集
    public  static void addAttachmentsWebDriver( WebDriver webDriver,String xpathValue, String xpathKey, String basePath,
                                                 Map detailData, OkHttpClient okHttpClient) {
        List<WebElement> attachment = webDriver.findElements(By.xpath(xpathValue));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        if (CollectionUtils.isNotEmpty(attachment)) {
            List<Attachment> attachments = new ArrayList<>();
            for (WebElement e : attachment) {
                try {
                    String href = e.getAttribute("href");
                    String name = e.getText();
                    if (Util.isFile(href, okHttpClient)) {
                        Attachment atta = new Attachment();
                        atta.setUrl(href);
                        atta.setTitle(name);
                        atta.setAttachmentsStatus(0);
                        String filePath = download(href, atta, basePath, okHttpClient);
                        if (!StringUtils.isEmpty(filePath)) {
                            //String localUrl = filePath.replace(basePath,localAttachmentsUrl);
                            setAttribute(e, "href", filePath, js);
                        }
                        atta.setMyUrl(filePath);
                        attachments.add(atta);
                    } else {
                        LOG.warn("{} not attachment", detailData.get("testUrl"));
                    }
                } catch (Exception ee) {
                    LOG.warn("下载附件失败 {}", webDriver.getCurrentUrl(), ee);
                }
            }
            if (!CollectionUtils.isEmpty(attachments)) {
                detailData.put(xpathKey,JSON.toJSONString(attachments));
            }
        }
    }

    public static void setAttribute(WebElement element, String attName, String attValu, JavascriptExecutor driver) {
        driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attName, attValu);
    }

    //webClient的图片采集
    public static void addImgs(Map detailData, HtmlPage htmlPage, String xpathValue, String xpathKey, String basePath,
                               String localImgsUrl, OkHttpClient client) {
        List<Img> imgs = new LinkedList<>();
        List<DomElement> imgList = htmlPage.getByXPath(xpathValue);
        for (DomElement dom : imgList) {
            try {
                Img img = new Img();
                img.setImgsStatus(0);
                String imgHref = dom.getAttribute("src");
                imgHref = getUrl(imgHref, htmlPage);
                if (!StringUtils.isBlank(imgHref)) {
                    String imgNew = download(imgHref, img, basePath, client);
                    if (!StringUtils.isBlank(imgNew)) {
                        String localPath = imgNew.replace(basePath, localImgsUrl);
                        dom.setAttribute("localPath", localPath);
                    } else {
                        dom.setAttribute("localPath", "");
                    }
                    dom.setAttribute("src", imgNew);

                    dom.setAttribute("originSrc", imgHref);
                    img.setCurrentPath(imgNew);
                    img.setOriginPath(imgHref);
                    imgs.add(img);
                }
            } catch (Exception ee) {
                LOG.warn("下载附件失败 {}", htmlPage.getUrl(), ee);
            }
        }
        if (!CollectionUtils.isEmpty(imgs)) {
            detailData.put(xpathKey, JSON.toJSONString(imgs));

        }
    }

    //webDriver的图片采集
    public static void addImgs(Map detailData, WebDriver webDriver,String xpathValue, String xpathKey,
                               String basePath,String localImgsUrl,OkHttpClient client) {
        List<Img> imgs = new LinkedList<>();
        List<WebElement> imgList = webDriver.findElements(By.xpath(xpathValue));
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        for (WebElement dom : imgList) {
            Img img = new Img();
            img.setImgsStatus(0);
            String imgHref = dom.getAttribute("src");
            if (!StringUtils.isBlank(imgHref)) {
                String imgNew = download(imgHref,img, basePath,client);
                if (StringUtils.isNoneBlank(imgNew)) {
                    String localPath = imgNew.replace(basePath, localImgsUrl);
                    setAttribute(dom, "src", imgNew, js);
                    setAttribute(dom, "localPath", localPath, js);
                    setAttribute(dom, "originSrc", imgHref, js);
                    img.setCurrentPath(imgNew);
                    img.setOriginPath(imgHref);
                    imgs.add(img);
                }
            }
        }
        if (!CollectionUtils.isEmpty(imgs)) {
            detailData.put(xpathKey,JSON.toJSONString(imgs));
        }
    }


    /**
     * @param href
     * @param htmlPage
     * @return 通过webclient获取url在页面的绝对路径
     */
    public static  String getUrl(String href, HtmlPage htmlPage) {
        try {
            String hrefNew =  "";
            hrefNew = htmlPage.getFullyQualifiedUrl(href).toString();
            return hrefNew;
        } catch (MalformedURLException e1) {
            LOG.error("获取页面绝对路径错误 {}", href);
        }
        return null;
    }


    // 根据图片网络地址下载附件
    public   static String download(String url,Attachment attachment,String path,OkHttpClient client) {
        byte[] size = new byte[2048];
        StringBuilder sb1 = new StringBuilder(url);
        String suffiname = sb1.substring(sb1.lastIndexOf("."), sb1.length());
        String filename = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder(path);
        File dirFile = new File(path);
        String fileName = sb.append(filename).append(suffiname).toString();
        try {
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        } catch (Exception e) {
            LOG.error("创建文件夹失败", e);
        }
        String downloadUrl = new String(url);
        LOG.info("开始执行下载附件-> {}", downloadUrl);
        FileChannel channel = null;
        FileOutputStream fos = null;
        Response response = null;
        InputStream in = null;
        try {
            Request request = new Request.Builder().url(downloadUrl).build();
            if (downloadUrl.contains("https")){
                response = client.newBuilder().readTimeout
                        (20, TimeUnit.SECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                        .build()
                        .newCall(request)
                        .execute();
            }
            else{
                response = client.newCall(request).execute();
            }
            if (response.isSuccessful()) {

                in = response.body().byteStream();
                File file = new File(fileName);
                fos = new FileOutputStream(file);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel = fos.getChannel();
                int num = 0;
                while ((num = in.read(size)) != -1) {
                    for (int i = 0; i < num; i++) {
                        buffer.put(size[i]);
                        buffer.flip(); // 此处必须要调用buffer的flip方法
                        channel.write(buffer);
                        buffer.clear();
                    }
                }
                attachment.setAttachmentsStatus(1);
            } else {
                attachment.setAttachmentsStatus(2);
            }
            return fileName;

        } catch (Exception e) {
            LOG.error("下载文件失败 {},{}", e.getMessage(), downloadUrl);
            attachment.setAttachmentsStatus(2);
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in !=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // 根据图片网络地址下载图片
    public  static String download(String url,Img img,String path,OkHttpClient client) {
        byte[] size = new byte[2048];
        StringBuilder sb1 = new StringBuilder(url);
        String suffiname = sb1.substring(sb1.lastIndexOf("."), sb1.length());
        String filename = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder(path);
        File dirFile = new File(path);
        String fileName = sb.append(filename).append(suffiname).toString();
        try {
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        } catch (Exception e) {
            LOG.error("创建文件夹失败", e);
        }
        String downloadUrl = new String(url);
        LOG.info("开始执行下载附件-> {}", downloadUrl);
        Response response = null;
        FileChannel channel = null;
        FileOutputStream fos = null;
        try {
            Request request = new Request.Builder().url(downloadUrl).build();
            if (downloadUrl.contains("https")) {
                response = client.newBuilder().readTimeout
                        (30, TimeUnit.SECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                        .build()
                        .newCall(request)
                        .execute();
            }
            else {
                response = client.newCall(request).execute();
            }
            if (response.isSuccessful()) {
                InputStream in = response.body().byteStream();
                File file = new File(fileName);
                fos = new FileOutputStream(file);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel = fos.getChannel();
                int num = 0;
                while ((num = in.read(size)) != -1) {
                    for (int i = 0; i < num; i++) {
                        buffer.put(size[i]);
                        buffer.flip(); // 此处必须要调用buffer的flip方法
                        channel.write(buffer);
                        buffer.clear();
                    }
                }


                img.setImgsStatus(1);
            } else {
                img.setImgsStatus(2);
            }
            return fileName;

        }catch (Exception e) {
            LOG.error("下载文件失败 {},{}", e.getMessage(), downloadUrl);
            img.setImgsStatus(2);
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
