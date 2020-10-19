package com.cetcbigdata.varanus.dao;

import com.cetcbigdata.varanus.entity.ListCrawlerdata;
import com.cetcbigdata.varanus.entity.OfficialDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Deprecated
@Repository
public interface OfficialDocumentDAO extends JpaRepository<OfficialDocument,Integer>, JpaSpecificationExecutor<OfficialDocument> {
    @Query(value = "select max(key_id) from official_document", nativeQuery = true)
    int findOfficialDocumentMaxKey();
    @Query(value = "select count(key_id) from official_document where is_clean = 0", nativeQuery = true)
    int findOfficialDocumentClean();

    @Query(value = "select new com.cetcbigdata.varanus.entity.ListCrawlerdata" +
            "(o.keyId,o.department,o.title,o.pubOfficeInfo,o.pubDateInfo,o.policyDate,o.insertDate) " +
            "from  OfficialDocument o order by insertDate desc ")
    Page<ListCrawlerdata> queryListofficial(Pageable pageable);


    @Query(value = "select new com.cetcbigdata.varanus.entity.ListCrawlerdata" +
            "(o.keyId,o.department,o.title,o.pubOfficeInfo,o.pubDateInfo,o.policyDate,o.insertDate) " +
            "from  OfficialDocument o where o.department=?1 and o.sectionTitle=?2 order by insertDate desc ")
    Page<ListCrawlerdata> queryListofficial2(String department, String sectionTitle, Pageable pageable);




    @Query(value = "select new com.cetcbigdata.varanus.entity.ListCrawlerdata" +
            "(o.keyId,o.department,o.title,o.pubOfficeInfo,o.pubDateInfo,o.policyDate,o.insertDate) " +
            "from  OfficialDocument o where o.department=?1 and o.sectionTitle=?2 order by insertDate desc ")
    Page<ListCrawlerdata> queryListofficial4(String department, String sectionTitle, Pageable pageable);

    int deleteOfficialDocumentByTaskIdAndListId(Integer taskId, Integer listId);

    @Query(value = "SELECT url FROM official_document where doc_id=?1 ORDER BY rand() LIMIT 5", nativeQuery = true)
    List<String> findUrlByDocId(Integer taskId);

    OfficialDocument findOfficialDocumentByKeyId(String keyId);

}
