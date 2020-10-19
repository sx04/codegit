package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.SysDictEntity;

import com.cetcbigdata.varanus.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/8/28 13:22
 */
@Repository
public interface SysDictDAO extends JpaRepository<SysDictEntity, Integer> {

    //获取来源信息
    @Query(value = "select * from sys_dict where type_code='dataSrcType'", nativeQuery = true)
    List<SysDictEntity> queryDataSrcType();
    //获取领域信息
    @Query(value = "select * from sys_dict where type_code='domain'", nativeQuery = true)
    List<SysDictEntity> queryDomain();

}
