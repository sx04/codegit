package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sunjunjie
 * @date 2020/9/7 15:59
 */
public interface AuditLogDAO extends JpaRepository<AuditLogEntity, Integer> {
}
