package cn.edu.hhu.a34backend.controller;

import cn.edu.hhu.a34backend.dto.SearchResult;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.ESService;
import cn.edu.hhu.a34backend.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("query")
public class QueryController
{
    @Autowired
    private ESService esService;

    @GetMapping("keywords")
    public Result queryKeywords(String queryString)
    {
        SearchHits<PdfDocPage> hitPages=esService.queryKeywords(queryString);
        List<SearchResult> searchResults=new ArrayList<>();
        for(SearchHit<PdfDocPage> hitPage: hitPages)
        {
            List<String> hitStrings=hitPage.getHighlightField("content");
            int pageNumber=hitPage.getContent().getPageNumber();
            long parentPdfUuid=hitPage.getContent().getParentPdfUuid();

            SearchResult searchResult=new SearchResult(hitStrings,pageNumber,parentPdfUuid);

            searchResults.add(searchResult);
        }
        return Result.success(searchResults,"成功");
    }


}
