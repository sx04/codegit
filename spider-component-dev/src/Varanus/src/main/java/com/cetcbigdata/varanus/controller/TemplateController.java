package com.cetcbigdata.varanus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.entity.*;
import com.cetcbigdata.varanus.service.TemplateService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;


/**
 * @author sunjunjie
 * @date 2020/8/17 17:24
 */

@RestController
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    private static final Logger LOG = LoggerFactory.getLogger(TemplateController.class);

    //模板第一个TAB页中来源和领域数据列表
    @GetMapping("template/task/domainAnddataSrcType")
    public Object templateDomainAnddataSrcType() {
        return templateService.domainAnddataSrcType();
    }

    //模板启动
    @PostMapping("template/run")
    public ErrorCode  templateRun(@RequestParam("templateId") int templateId,@RequestParam("isRun") int isRun){
        int userId=1;
        return templateService.templateRun(templateId,isRun,userId);
    }

    //模板删除
    @PostMapping("template/delete")
    public ErrorCode  templateDelete(@RequestParam("templateId") int templateId){
        int userId=1;
        return templateService.templateDelete(templateId, userId);
    }

    //模板爬取数据清除
    @PostMapping("template/clean")
    public ErrorCode  templateClean(@RequestParam("templateId") int templateId){
        int userId=1;
        return templateService.templateClean(templateId,userId);
    }

    //模板拆分
    @GetMapping("template/break")
    public ErrorCode  templateBreak(@RequestParam("templateId") int templateId){
        int userId=1;
        return templateService.templateBreak(templateId,userId);
    }


    //模板基本信息保存
    @PostMapping("template/basic/save")
    public HashMap templateBasicSave(@RequestBody TemplateBasicEntity templateBasicEntity){
        int userId = 1;
        int taskId = templateBasicEntity.getTaskId();
        int isRun = templateBasicEntity.getIsRun();
        int pageNumber = templateBasicEntity.getPageNumber();
        int docNumber = templateBasicEntity.getDocNumber();
        String templateUrl = templateBasicEntity.getTemplateUrl();
        int id = templateBasicEntity.getId();
        String srcTypeCode = templateBasicEntity.getSrcTypeCode();
        String domainCode = templateBasicEntity.getDomainCode();
        int templateId =templateService.templateBasicSave(userId,isRun,pageNumber,docNumber,templateUrl,id,taskId,srcTypeCode,domainCode);
        HashMap map = new HashMap();
        map.put("templateId",templateId);
        return map;
    }

    //模板列表信息保存
    @PostMapping("template/list/save")
    public HashMap templateListSave(@RequestBody ConfigListEntity configListEntity){
        int userId=1;
        int listId = templateService.templateListSave(configListEntity,userId);
        HashMap map = new HashMap();
        map.put("listId",listId);
        return map;
    }

    //模板详情信息保存
    @PostMapping("template/detail/save")
    public ErrorCode templateDetailSave(@RequestBody(required=false) String json){
        int userId = 1;
        JSONObject jsonObject = JSON.parseObject(json);
        String templateDetail = jsonObject.getString("templateDetailEntity");
        String templateDynamic =jsonObject.getString("templateDetailDynamic");
        HashMap<String, String> detailMap=JSON.parseObject(templateDetail, HashMap.class);
        HashMap<String,String> dynamicMap=JSON.parseObject(templateDynamic, HashMap.class);
        return templateService.templateDetailSave(detailMap,dynamicMap,userId);
    }

    //新增模板信息
    @PostMapping("template/more/save")
    public HashMap templateMorelSave(@RequestParam("taskId") int taskId){
        int userId = 1;
        HashMap map = new HashMap();
        int ti = templateService.templateMorelSave(taskId,userId);
        map.put("templateId",ti);
        return map;
    }

    //模板信息复制
    @GetMapping("template/copy")
    public HashMap templateCopy(@RequestParam("selfTemplateId") int selfTemplateId,
                                @RequestParam("othertemplateId") int othertemplateId)
                                {
        HashMap map = new HashMap();
        int listId = templateService.copyConfigList(othertemplateId);
        map.put("listId",listId);
        map.put("templateId",selfTemplateId);
        return map;
    }

    //模板信息共用
    @GetMapping("template/setGroup")
    public HashMap templateSetGroup(@RequestParam("selfTemplateId") int selfTemplateId,
                                    @RequestParam("othertemplateId") int otherTemplateId){
        int userId =1 ;
        HashMap map = new HashMap();
        templateService.templateSetGroup(selfTemplateId,otherTemplateId,userId);
        map.put("templateId",selfTemplateId);
        return map;
    }

    //获取模板信息概要列表
    @GetMapping("template/query")
    public Object templateQuery(@RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                 @RequestParam(value="json",defaultValue = "{}") String json){
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            JSONObject jsonObject = JSON.parseObject(json);
            TemplateQueryEntity templateQueryEntity = JSON.toJavaObject(jsonObject, TemplateQueryEntity.class);
            Page od = templateService.templateQuery(pageable,templateQueryEntity);
            return od;
        } catch (Exception e) {
            LOG.error("查询模板信息出错", e);
        }
        return "";
    }

    //查询模板的第一个TAB页模板基础信息
    @GetMapping("template/one/basic")
    public TemplateOneEntity templateOneBasic(@RequestParam(value = "templateId") int templateId){
        int i =templateService.groupNumber(templateId);
        TemplateOneEntity  templateOneEntity =  templateService.templateOneBasic(templateId);
        templateOneEntity.setCommonNumber(i);
        return templateOneEntity;
    }

    //查询模板的第二个TAB页模板列表信息
    @GetMapping("template/one/list")
    public ConfigListEntity templateOneList(@RequestParam(value = "groupId") int groupId){
        return templateService.templateOneList(groupId);
    }

    //查询模板的第三个TAB页模板详情信息
    @GetMapping("template/one/detail")
    public HashMap templateOneDetail(@RequestParam(value = "listId") int listId){
        return templateService.templateOneDetail(listId);
    }

    //模板列表数据查询（进行导入操作）
    @GetMapping("template/one/listImport")
    public Object templateOneImport( @RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer size,
                                     @RequestParam(value="json",defaultValue = "{}") String json)
    {
        try {
            Pageable pageable = new PageRequest(page - 1, size);
            Page od = templateService.templateImportQuery(pageable,json);
            return od;
        } catch (Exception e) {
            LOG.error("查询失败", e);
        }
        return "";
    }

    //模板填写时列表验证
    @PostMapping("template/list/verify")
    public Object templateListVerify(@RequestParam("json") String json)  {
        if (StringUtils.isBlank(json)) {
            return ErrorCode.PARAMETER_ERROR;
        }
        try {
            ConfigListEntity configListEntity = JSON.parseObject(json, ConfigListEntity.class);
            if (configListEntity != null) {
                return templateService.templateListVerify(configListEntity);
            }
            return ErrorCode.PARAMETER_ERROR;
        } catch (Exception e) {
            return ErrorCode.TEMPLATELIST_VERTIFY_ERROR;
        }
    }


    //模板填写时文章详情验证
    @PostMapping("template/detail/verify")
    public Object templateDetailVerify(@RequestParam("json") String json) {
        if (StringUtils.isBlank(json)) {
            return ErrorCode.PARAMETER_ERROR;
        }
        try {
            return templateService.templateDetailVerify(json);
        } catch (Exception e) {
            return ErrorCode.TEMPLATEDETAIL_VERTIFY_ERROR;
        }
    }


    //导入其他模板前验证
    @GetMapping("template/import/verify")
    public Object templateImportVerify(@RequestParam("templateId") int templateId,@RequestParam("listUrl") String listUrl,
                                      @RequestParam("detailUrl") String detailUrl) {

        if (templateId > 0) {
            if ((StringUtils.isBlank(listUrl) || StringUtils.isBlank(detailUrl))) {
                return ErrorCode.PARAMETER_ERROR;
            }
            try {
                return templateService.templateImportVerify(templateId, listUrl, detailUrl);
            } catch (Exception e) {
                return ErrorCode.TEMPLATEALL_VERTIFY_ERROR;
            }
        }
        return ErrorCode.PARAMETER_ERROR;
    }

    //模板录入后总体验证
    @GetMapping("template/isCorrect")
    public Object templateIsCorrect(@RequestParam("templateId") int templateId,
                                    @RequestParam("isCorrect") int isCorrect)  {
        int userId = 1 ;
        return templateService.templateIsCorrect(templateId,isCorrect,userId);
    }

    //模板录入后总体验证
    @GetMapping("template/total/verify")
    public Object templateTotalVerify(@RequestParam("templateId") int templateId) {
        if (templateId > 0) {
            try {
                return templateService.templateTotalVerify(templateId);
            } catch (Exception e) {
                return ErrorCode.TEMPLATEALL_VERTIFY_ERROR;
            }
        }
        return ErrorCode.PARAMETER_ERROR;
    }
}