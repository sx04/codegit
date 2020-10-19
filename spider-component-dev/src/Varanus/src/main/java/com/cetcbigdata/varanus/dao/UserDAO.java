package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.SysUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author sunjunjie
 * @date 2020/8/28 14:27
 */
public interface UserDAO extends JpaRepository<SysUserEntity, Integer> {

    @Query(value = "select template set group_id=?1 where id=?2 ", nativeQuery = true)
    int setGroupId (int groupId,int templateId);


}
