package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface SearchService
{
    Result searchInContent(String keywords, SearchCondition condition, Pageable pageRequest) throws IOException;

    Result searchInAbstract(String keywords, SearchCondition condition, Pageable pageRequest);

    Result searchInImageText(String keywords, SearchCondition condition, Pageable pageRequest);

    Result searchSuggest(String keywards);
}
