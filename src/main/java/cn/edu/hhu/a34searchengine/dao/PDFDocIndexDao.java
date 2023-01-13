package cn.edu.hhu.a34searchengine.dao;


import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;


public interface PDFDocIndexDao
{

    SearchPage<PDFDoc> searchInContent(String keywords, SearchCondition condition, Pageable pageRequest);

    SearchPage<PDFDoc> searchInAbstracts(String keywords, SearchCondition condition, Pageable pageRequest);
    SearchPage<PDFDoc> searchInImageTexts(String keywords, SearchCondition condition, Pageable pageRequest);
}
