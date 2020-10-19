package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.core.DispatcherListCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-4-22
 * Time:13:40
 */
@Service
public class TaskBasicInfoService {

    @Autowired
    private DispatcherListCrawler dispatcherListCrawler;

    @Async
    public void tt() {
        try {
            dispatcherListCrawler.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
