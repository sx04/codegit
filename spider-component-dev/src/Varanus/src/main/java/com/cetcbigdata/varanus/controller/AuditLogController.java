package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.common.ResponseBean;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.entity.AuditlogModel;
import com.cetcbigdata.varanus.service.AuditLogService;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/9/4 16:52
 */

@RestController
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogController.class);

    @ApiOperation(value = "获取审计日志筛选信息", httpMethod = "POST")
    @PostMapping("auditlog/query")
    public Object getAuditlog(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                    @RequestParam(value = "json", defaultValue = "{}") String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            AuditlogModel auditlogModel = JSON.toJavaObject(jsonObject, AuditlogModel.class);
            return new ResponseBean(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), auditLogService.auditLogQuery(pageable, auditlogModel));
        } catch (Exception e) {
            LOG.error("查询审计日志出错", e);
        }
        return new ResponseBean(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), "");
    }

    @ApiOperation(value = "获取审计日志操作模块", httpMethod = "GET")
    @GetMapping("auditlog/operateModule")
    public ResponseBean getoperateModule(){
        List auditlogOperrateModules= auditLogService.getoperateModule();
        return new ResponseBean(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), auditlogOperrateModules);
    }

}
