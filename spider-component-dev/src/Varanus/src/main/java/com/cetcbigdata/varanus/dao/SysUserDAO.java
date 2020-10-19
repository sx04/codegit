package com.cetcbigdata.varanus.dao;


import com.cetcbigdata.varanus.entity.SysUserEntity;
import com.cetcbigdata.varanus.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
/**
 * @author sunjunjie
 * @date 2020/8/11 14:48
 */
@Repository
public interface SysUserDAO extends JpaRepository<UserEntity, Integer> {

    //获取单一项目的用户信息
    @Query(value = "SELECT id,user_name,is_admin from sys_user where id in(SELECT user_id from project_user where project_id=?1)", nativeQuery = true)
    List<UserEntity> queryProjectUsers(String code);

    //删除单一项目的所有用户(修改项目用户信息时先执行删除再执行新增操作)
    @Modifying
    @Query(value = "DELETE FROM project_user WHERE project_id =?1", nativeQuery = true)
    int deleteProjectUser(String code);


    //根据用户名查询用户id
    @Query(value = "SELECT id from sys_user where user_name =?1", nativeQuery = true)
    String queryUserIdByName(String name);

    //单一项目新增用户
    @Modifying
    @Query(value = "insert into project_user(user_id,project_id) values(?1,?2)", nativeQuery = true)
    int addProjectUser(int id,String code);

    //查询用户名
    @Query(value = "SELECT * from sys_user", nativeQuery = true)
    List<UserEntity> queryUser();

}
