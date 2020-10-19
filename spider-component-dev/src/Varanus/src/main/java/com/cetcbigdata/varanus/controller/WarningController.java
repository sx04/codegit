package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.service.AuditLogService;
import com.cetcbigdata.varanus.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * @author sunjunjie
 * @date 2020/9/1 9:12
 */
@RestController
public class WarningController {

    @Autowired
    private WarningService warningService;

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(WarningController.class);

    //查询模板报警数据列表
    @GetMapping("query/taskWarning/list")
    public Object queryTaskWarningList(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                       @RequestParam(value="json",defaultValue = "{}") String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            Map maps = (Map) JSON.parse(json);
            Page od = warningService.queryTaskWarningList(pageable,maps);
            return od;
        } catch (Exception e) {
            LOG.error("查询模板报错信息出错", e);
        }
        return "";
    }

    //模板填写者查询自身的报警数据列表
    @GetMapping("query/admin/warningList")
    public Object queryAdminWarningList() {
        int userId =0;
       return  warningService.queryUserWarningList(userId);
    }

    //项目分配者者查询报警数据列表
    @GetMapping("query/user/warningList")
    public Object queryUserWarningList(@RequestParam("userId") int userId) {
        return  warningService.queryUserWarningList(userId);
    }


    //查询指定模板下报警数据列表
    @GetMapping("query/warningOne/list")
    public Object queryWarningList(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                   @RequestParam(value = "id") String templateId,@RequestParam(value = "Data") String data) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            Page od = warningService.queryWarningOneList(pageable, templateId,data);
            return od;
        } catch (Exception e) {
            LOG.error("查询模板报错信息出错", e);
        }
        return "";
    }

    //改变报警状态为已修改
    @PostMapping("update/warning/state")
    public Object updateWarningState(@RequestParam(value = "templateId") Integer templateId,
                                     @RequestParam(value = "userId") Integer userId){

        int i = warningService.updateWarningState(templateId,userId);
        if (i > 0) {
            try {
                auditLogService.saveInfo(userId, SysModuleEnum.WARNING, "修改",
                        "templateId为"+templateId+"的报警数据状态为已处理", "");
            } catch(Exception e){
                LOG.error("修改"+SysModuleEnum.TEMPLATE.getName()+"日志失败！");
            }
            return ErrorCode.SUCCESS;
        } else {
            return ErrorCode.WARNING_UPDATE_ERROR;
        }
    }
}
