package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.entity.ProjectNumCount;
import com.cetcbigdata.varanus.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * @author sunjunjie
 * @date 2020/8/31 14:55
 */
//统计爬虫整体数据
@RestController
public class StatisticsController {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;


    //查询采集数据列表
    @GetMapping("query/data/list")
    public Object queryDataList(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                @RequestParam(value = "json", defaultValue = "{}") String json) {

        try {
            Pageable pageable = new PageRequest(page - 1, size);
            Map maps = (Map)JSON.parse(json);
                Page od = statisticsService.queryDataList(pageable, maps);
                return od;
        } catch (Exception e) {
            LOG.error("查询任务信息出错", e);
        }
        return "";
    }

    //查询单条采集数据详情列表
    @GetMapping("query/data/one")
    public Object queryDataOne(@RequestParam(value = "id") String id,@RequestParam(value = "projectName")String projectName) {
        return statisticsService.queryDataOne(id,projectName);
    }

    //统计项目的任务完成的数量
    @GetMapping("project/task/num")
    public Object projectTaskNum() {
        return statisticsService.projectTaskNum();
    }

    //首页统计项目的采集和配置信息
    @GetMapping("query/user/projectCount")
    public Object projectCountNum(@RequestParam("userId") int id) {
        return statisticsService.projectCountNum(id);
    }

    //首页统计模板的配置情况
    @GetMapping("query/admin/templateCount")
    public Object queryAdminTemplateCount() {
        return statisticsService.queryAdminTemplateCount();
    }

   /* //项目数据查询
    @GetMapping("query/admin/dataTemplateCount")
    public Object queryAdminDataTemplateCount() {
        return statisticsService.queryDataTemplateCountById();
    }
*/
    //按日期查询项目数据
    @GetMapping("query/admin/dataTemplateByDate")
    public Object queryAdminDataTemplateByDate(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate,
                                               @RequestParam(value = "code") String code) {
        return statisticsService.queryAdminDataTemplateByDate(startDate,endDate,code);
    }

    //项目数据折线图显示
    @GetMapping("query/admin/echartData")
    public Object queryEchartDataList(@RequestParam(value = "time") String time) {
        Map<String, ProjectNumCount> projectMap = statisticsService.queryDataTemplateCountById();
        Map<String, ProjectNumCount> projectecharttMap = statisticsService.queryEchartDataList(time,projectMap);
        return projectecharttMap;
    }
}
