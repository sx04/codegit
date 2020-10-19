package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.entity.TaskQueryEntity;
import com.cetcbigdata.varanus.service.AuditLogService;
import com.cetcbigdata.varanus.service.TaskService;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/8/17 9:24
 */

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    //任务保存
    @ApiOperation(value = "图片上传", httpMethod = "POST")
    @ApiImplicitParam(name = "uploadFile",value = "图片",paramType = "formData",required = true,dataType = "file")
    @PostMapping("task/save")
    public String taskSave(@RequestParam(value = "json", defaultValue = "{}") String json,@RequestParam("uploadFile") MultipartFile uploadFile) {
        int userId =1;
        JSONObject jsonObject = JSON.parseObject(json);
        TaskInfoEntity taskInfoEntity = JSON.toJavaObject(jsonObject, TaskInfoEntity.class);
        String s = taskService.taskSave(taskInfoEntity,userId,uploadFile);
        return s;
    }

    @PostMapping("task/delete")
    public Object taskDelete(@RequestParam("taskId") int id) {
        ErrorCode ec = ErrorCode.TASK_DELETE_ERROR;
        try {
            int userId = 1;
            ec =taskService.taskDelete(id);
            auditLogService.saveInfo(userId, SysModuleEnum.TASK, "删除",
                    "删除的任务id:"+id,"");
        } catch (Exception e) {
            LOG.error("删除" + SysModuleEnum.TASK.getName() + "日志失败！");
        }
        return ec;
    }

     //任务领取
    @PostMapping("task/take")
    public Object taskTake(@RequestParam("taskIds") String[] taskIds, @RequestParam("userId") int userId) {
        int i = taskService.taskTake(taskIds, userId);
        if (i > 0) {
            return ErrorCode.SUCCESS;
        }
        return ErrorCode.TASK_TAKE_ERROR;
    }

    @GetMapping("task/query")
    public Object taskQuery(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                            @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                            @RequestParam(value = "json", defaultValue = "{}") String json) {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            TaskQueryEntity taskQueryEntity = JSON.toJavaObject(jsonObject, TaskQueryEntity.class);
            Page od = taskService.taskQuery(pageable, taskQueryEntity);
            return od;
        } catch (Exception e) {
            LOG.error("查询任务信息出错", e);
        }
        return "";
    }

    //查询所有项目名
    @GetMapping("task/queryProjectName")
    public List<String> queryProjectName() {
        return taskService.queryProjectName();
    }

    //模糊查询所有网站名
    @GetMapping("task/queryWebName")
    public List<String> queryWebName(@RequestParam("webName") String webName) {
        return taskService.queryWebName(webName);
    }

    //根据网站名查询板块名
    @GetMapping("task/querySectiontitle")
    public List<String> querySectiontitle(@RequestParam("webName") String webName) {
        return taskService.querySectiontitle(webName);
    }

    //根据任务id查询任务
    @GetMapping("task/queryTaskById")
    public TaskInfoEntity queryTaskById(@RequestParam("taskId") int taskId) {
        return taskService.queryTaskById(taskId);
    }

    //统计用户任务完成的数量
    @GetMapping("task/finish/num")
    public Object taskFinishNum(@RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
                                @RequestParam(value = "user", required = false) String user, @RequestParam(value = "projectName", required = false) String projectName) {
        try {
            return taskService.taskFinishNum(startDate, endDate, user, projectName);
        } catch (Exception e) {
            LOG.error("查询工作量出错", e);
            return ErrorCode.USERTASK_FIND_ERROR;
        }
    }

    //Excel任务上传
    @ApiOperation(value = "文档上传", httpMethod = "POST")
    @ApiImplicitParam(name = "uploadFile",value = "Excel文件",paramType = "formData",required = true,dataType = "file")
    @PostMapping(value ="task/excel/import", headers = "content-type=multipart/form-data")
    public Object importExcel(@RequestParam("uploadFile") MultipartFile uploadFile,
                              @RequestParam(value = "projectId") String projectId){
        int userId= 1;
        if (uploadFile.isEmpty()) {
            return ErrorCode.FILE_SIZE_ZERO;
        }
        String fileName = uploadFile.getOriginalFilename().toLowerCase();
        if (fileName == null && "".equals(fileName)) {
            return ErrorCode.FILE_GET_FAIL;
        } else {
            if (fileName.endsWith("xls") || fileName.endsWith("xlsx")) {
                Boolean flag = taskService.importExcel(uploadFile,projectId);
                if (flag) {
                    try {
                        auditLogService.saveInfo(userId, SysModuleEnum.TASK, "新增",
                                "通过Excel新增任务", "");
                    } catch (Exception e) {
                        LOG.error("新增" + SysModuleEnum.TASK.getName() + "日志失败！");
                    }
                    return ErrorCode.SUCCESS;
                } else {
                    return ErrorCode.FILE_UPLOAD_FAIL;
                }
            }
            return ErrorCode.FILE_TYPE_LIMIT;
        }
    }

}


