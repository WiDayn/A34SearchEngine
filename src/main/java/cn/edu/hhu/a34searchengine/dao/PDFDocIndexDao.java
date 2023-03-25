package cn.edu.hhu.a34searchengine.dao;


import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.vo.SuggestResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.io.IOException;


public interface PDFDocIndexDao
{

    SearchPage<PDFDoc> searchInContent(String keywords, SearchCondition condition, Pageable pageRequest);

    SearchPage<PDFDoc> searchInAbstract(String keywords, SearchCondition condition, Pageable pageRequest);
    SearchPage<PDFDoc> searchInImageText(String keywords, SearchCondition condition, Pageable pageRequest);

    SuggestResult searchPhraseSuggest(String keywords) throws IOException;
}
