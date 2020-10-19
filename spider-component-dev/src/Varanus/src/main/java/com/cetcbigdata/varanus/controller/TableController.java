package com.cetcbigdata.varanus.controller;

import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.entity.TableColumnInfoEntity;
import com.cetcbigdata.varanus.entity.TableDetailConfigEntity;
import com.cetcbigdata.varanus.entity.TaskInfoEntity;
import com.cetcbigdata.varanus.entity.TemplateBasicEntity;
import com.cetcbigdata.varanus.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author sunjunjie
 * @date 2020/9/16 14:27
 */
@RestController
public class TableController {

    @Autowired
    private TableService tableService;

    //查询表格类模板的第一个TAB页模板基础信息
    @GetMapping("table/one/basic")
    public Object tableOneBasic(@RequestParam(value = "taskId") int taskId){ return  tableService.tableOneBasic(taskId);
    }

    //查询表格类模板的第一个TAB页模板的图片信息
    @GetMapping(value ="table/one/basic/imag",produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] tableOneBasicImag(@RequestParam(value = "taskId") int taskId){
        TaskInfoEntity taskInfoEntity =  tableService.tableOneBasic(taskId);
        byte[] imag = taskInfoEntity.getTableInfoImag();
        return imag;
    }

    //查询表格类模板的第二个TAB页数据提取信息
    @GetMapping("table/one/dataExtraction")
    public Object tableDataExtraction(@RequestParam(value = "taskId") int taskId){
        return tableService.tableDataExtraction(taskId);
    }

    //查询表格类模板的第三个TAB页数据存储
    @GetMapping("table/one/dataStorage")
    public Object tableDataStorage(@RequestParam(value = "taskId") int taskId){
        return tableService.tableDataStorage(taskId);
    }

    //表格类模板的第一个TAB页模板基础信息保存
    @PostMapping("table/basic/save")
    public Object tableBasicSave(@RequestBody TaskInfoEntity taskInfoEntity){
        int userId=1;
        return tableService.tableBasicSave(taskInfoEntity,userId);
    }

     //表格类模板的第二个TAB页模板基础信息保存(该页面数据保存将会创建表格，如果表格存在则不创建)
     @GetMapping("table/dataExtraction/save")
     public Object tableDataExtractionSave(@RequestBody TableDetailConfigEntity tableDetailConfigEntity){
         int userId= 1;
         return tableService.tableDataExtractionSave(tableDetailConfigEntity,userId);
     }

    //表格类模板的第二个TAB页模板基础信息删除
    @GetMapping("table/dataExtraction/delete")
    public Object tableDataExtractionDelete(@RequestParam("id") int id){
        int userId= 1;
        return tableService.tableDataExtractionDelete(id,userId);
    }


    //表格类模板的第三个TAB页模板新增所需列
    @PostMapping("table/dataStorage/add")
    public Object tableDataStorageAdd(@RequestBody TableColumnInfoEntity tableColumnInfoEntity){
        int userId= 1;
        return tableService.tableDataStorageAdd(tableColumnInfoEntity,userId);
    }

    //表格类模板的第三个TAB页模板删除所需列
    @GetMapping("table/dataStorage/delete")
    public Object tableDataStorageDelete(@RequestParam("id") int id){
        int userId= 1;
        return tableService.tableDataStorageDelete(id,userId);
    }


    //表格类模板第二个Tab页的验证
    @GetMapping("table/dataStorage/verify")
    public Object tableDataExtractionVerify (@RequestBody TableDetailConfigEntity tableDetailConfigEntity){
        return tableService.tableDataExtractionVerify(tableDetailConfigEntity);
    }

    //表格类任务爬取数据清除
    @PostMapping("table/data/clean")
    public ErrorCode  tableDataClean(@RequestParam("taskId") int taskId){
        int userId=1;
        return tableService.tableDataClean(taskId,userId);
    }

}
