package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.dao.SysUserDAO;
import com.cetcbigdata.varanus.dao.UserDAO;
import com.cetcbigdata.varanus.entity.SysUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sunjunjie
 * @date 2020/8/28 13:52
 */
@Service
public class UserService {

    @Autowired
    private SysUserDAO sysUserDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AuditLogService auditLogService;

    static Logger LOG = LoggerFactory.getLogger(UserService.class);

    public Object userQueryAll(){
        return sysUserDAO.queryUser();
    }

    public void userAdd(SysUserEntity sysUserEntity,int userId) {
        userDAO.saveAndFlush(sysUserEntity).getId();
        String userName = sysUserEntity.getUserName();
        int id = sysUserEntity.getId();
        try {
            if(id>0){
                auditLogService.saveInfo(userId, SysModuleEnum.USER, "修改",
                        "被修改的用户id为" + id, "");
            } else {
                auditLogService.saveInfo(userId, SysModuleEnum.USER, "新增",
                        "新增用户名：" + userName, "");
            }
        } catch(Exception e){
            LOG.error("修改"+SysModuleEnum.USER.getName()+"日志失败！");
        }
    }

    public Object userQueryOne(int id){
        return userDAO.findOne(id);
    }
}
