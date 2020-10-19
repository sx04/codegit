package com.cetcbigdata.varanus.service;

import com.cetcbigdata.varanus.dao.AuditLogDAO;
import com.cetcbigdata.varanus.constant.SysModuleEnum;
import com.cetcbigdata.varanus.entity.AuditLogEntity;
import com.cetcbigdata.varanus.entity.AuditlogModel;
import com.cetcbigdata.varanus.exception.AuditLogException;
import com.cetcbigdata.varanus.utils.SqlPageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author sunjunjie
 * @date 2020/9/4 17:17
 */
@Service
public class AuditLogService {

    static Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    @PersistenceContext
    EntityManager em;

    @Autowired
    private AuditLogDAO auditLogDAO;

    /**
     * 审计日志数据存入
     */
    private void save(int userId, SysModuleEnum moduleCode, String operType, String content) {
        AuditLogEntity audit = new AuditLogEntity();
        audit.setUserId(userId);
        if (moduleCode != null) {
            audit.setModuleCode(moduleCode.getName());
        }
        Timestamp operTime = new Timestamp(new Date().getTime());
        audit.setOperTime(operTime);
        audit.setOperType(operType);
        if (content != null) {
            audit.setContent(content);
        }
        auditLogDAO.save(audit);
    }


    public void saveInfo(int userId, SysModuleEnum operateModule, String operType, String newContent
            ,String oldContent) throws AuditLogException {
        StringBuilder content = new StringBuilder();
        if (operType == "新增") {
            content.append("新增").append(operateModule.getName()).append(":").append(newContent);
        } else if (operType == "修改") {
            if (StringUtils.isBlank(oldContent)) {
                content.append("修改").append(operateModule.getName()).append(":").append(newContent);
            } else {
                content.append("修改").append(operateModule.getName()).append(":将[")
                        .append(oldContent).append("]修改为：[").append(newContent).append("]");
            }
        } else if (operType == "删除") {
            content.append("删除").append(operateModule.getName()).append(":").append(newContent);
        } else {
            content.append(newContent);
        }
        String text = content.toString();
        this.save(userId, operateModule, operType, text);
    }

        //按条件查询审计日志
        public Object auditLogQuery (Pageable pageable, AuditlogModel auditlogModel) throws AuditLogException {

            StringBuilder sql = new StringBuilder("select * from audit_log where 1=1");
            String endTime = auditlogModel.getEndTime();
            String startTime = auditlogModel.getStartTime();
            String userId = auditlogModel.getUserId();
            String moudle = auditlogModel.getOperateModule();
            String operateType = auditlogModel.getOperateType();

            if (StringUtils.isNoneBlank(userId)) {
                sql.append(" and user_id= '").append(userId).append("'");
            }

            if (StringUtils.isNoneBlank(moudle)) {
                sql.append("  and module_code= '").append(moudle).append("'");
            }

            if (StringUtils.isNoneBlank(operateType)) {
                sql.append("  and oper_type= '").append(operateType).append("'");
            }


            if (StringUtils.isNoneBlank(startTime)) {
                sql.append("  and oper_time >'").append(startTime).append("'");
            }

            if (StringUtils.isNoneBlank(endTime)) {
                sql.append("  and oper_time <'").append(endTime).append("'");
            }

            return SqlPageUtil.queryByConditionNQ(sql.toString(), pageable, em, AuditLogEntity.class);
        }


        //查询板块名称列表
        public List getoperateModule () {
            return SysModuleEnum.SysModuleEnum();
        }

        /**
         * 获取当前操作时间(String类型)
         *
         * @return
         */
       /* private static String getOperateTime () {
            String strTime = "";
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strTime = df.format(new Date());
            return strTime;

        }

        *//**
         * 将String日期类型转为date类型
         *
         * @return
         *//*
        public static Date strToDate () {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = getOperateTime();
            try {
                Date strtodate = formatter.parse(strDate);
                formatter.format(strtodate);
                return strtodate;
            } catch (Exception ex) {
                logger.error("search date format error:{}", ex.getMessage());
            }
            return null;
        }*/

    }
