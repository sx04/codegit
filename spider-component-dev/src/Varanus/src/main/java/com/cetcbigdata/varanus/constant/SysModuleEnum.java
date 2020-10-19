package com.cetcbigdata.varanus.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/9/4 17:30
 */
public enum SysModuleEnum {
    TEMPLATE("模板板块"),
    USER("用户板块"),
    PROJECT("项目板块"),
    WARNING("报警板块"),
    TASK("任务板块"),
    TABLE("表格类板块");

    private String name;
    private SysModuleEnum(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static List SysModuleEnum () {
        List  list = new ArrayList();
        for (SysModuleEnum sysModuleEnum : SysModuleEnum.values()) {
            list.add(sysModuleEnum.getName());
        }
        return list;
    }

}
