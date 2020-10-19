package com.cetcbigdata.varanus.parser;

import com.cetcbigdata.varanus.core.component.BaseDocParser;
import com.cetcbigdata.varanus.entity.DocDetail;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-3-21
 * Time:15:00
 * @author matthew
 */
@Component
public class JsonDocParser extends BaseDocParser {


    @Override
    protected OfficialDocument processDoc(DocDetail docDetail, OfficialDocument officialDocument, ThreadLocal<WebClient> webClient, OkHttpClient okHttpClient, WebRequest webRequest, Boolean needProxy) throws Exception {
        return null;
    }

    @Override
    protected void parsePage(DocDetail docDetail, OfficialDocument officialDocument, HtmlPage htmlPage, Response response) throws Exception {

    }
}
