package cn.edu.hhu.a34searchengine.controller;

import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.service.SearchService;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.Result;
import cn.edu.hhu.a34searchengine.vo.SearchResult;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("search")
public class SearchController
{

    @Autowired
    private SearchService searchService;



    @GetMapping("content")
    @JsonView(SearchResult.SearchResultView.class)
    public Result searchInContent(String queryString, SearchCondition condition, int page) throws IOException
    {
        Timer timer=new Timer();                //page从0开始,page==0是第一页,参数page=1时,访问的是第2页
        Result result=searchService.searchInContent(queryString,condition, PageRequest.of(page - 1,10)); //每页10个元素
        timer.stop();
        return result;
    }

    @GetMapping("abstract")
    @JsonView(SearchResult.SearchResultView.class)
    public Result searchInAbstract(String queryString, SearchCondition condition, int page)
    {                                                                                           //page从0开始,page==0是第一页,参数page=1时,访问的是第2页
        return searchService.searchInAbstract(queryString, condition, PageRequest.of(page - 1, 5));
    }

    @GetMapping("imageText")
    @JsonView(SearchResult.SearchResultView.class)
    public Result searchInImageText(String queryString, SearchCondition condition, int page)
    {                                                                                           //page从0开始,page==0是第一页,参数page=1时,访问的是第2页
        return searchService.searchInImageText(queryString,condition, PageRequest.of(page - 1,5));
    }


}
