package com.cetcbigdata.varanus.controller;

import com.cetcbigdata.varanus.constant.ErrorCode;
import com.cetcbigdata.varanus.entity.SysUserEntity;
import com.cetcbigdata.varanus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sunjunjie
 * @date 2020/8/28 13:49
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    //查询所有用户信息
    @GetMapping("user/query/all")
    public Object userQueryAll() {
        return userService.userQueryAll();
    }

    //用户新增
    @PostMapping("user/add")
    public Object userAdd(@RequestBody SysUserEntity sysUserEntity) {
        int userId=1;
         userService.userAdd(sysUserEntity,userId);
         return ErrorCode.SUCCESS;
    }

    //查询单一用户
    @PostMapping("user/query/one")
    public Object userQueryOne(@RequestParam("id") int id) {
        return userService.userQueryOne(id);
    }

}
