package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.ListCrawlerdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
@Deprecated
@Repository
public interface OfficialDocumentQueryDAO extends JpaRepository<ListCrawlerdata,Integer>, JpaSpecificationExecutor<ListCrawlerdata> {


}
