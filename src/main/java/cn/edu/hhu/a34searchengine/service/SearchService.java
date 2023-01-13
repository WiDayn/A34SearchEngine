package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
public interface SearchService
{
    Result searchInContent(String keywords, SearchCondition condition, Pageable pageRequest);

    Result searchInAbstracts(String keywords, SearchCondition condition, Pageable pageRequest);

    Result searchInImageTexts(String keywords, SearchCondition condition, Pageable pageRequest);
}
