package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PDFDocIndexDao;
import cn.edu.hhu.a34searchengine.dao.PDFIndexRepository;
import cn.edu.hhu.a34searchengine.dto.DocHit;
import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.pojo.PDFDocPage;
import cn.edu.hhu.a34searchengine.service.SearchService;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.Result;
import cn.edu.hhu.a34searchengine.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService
{
    @Autowired
    public PDFIndexRepository pdfDocRepository;

    @Autowired
    public PDFDocIndexDao pdfDocIndexDao;


    //将搜索结果装配到SearchResult
    private SearchResult _assembleSearchResult(SearchPage<PDFDoc> searchPage)
    {
        Timer timer=new Timer();
        SearchResult searchResult = new SearchResult();
        searchResult.setTotalDocCount(searchPage.getTotalElements());
        searchResult.setDocHits(new DocHit[searchPage.getSize()]);
        searchResult.setTotalPageCount(searchPage.getTotalPages());
        searchResult.setThisPageNumber(searchPage.getNumber() + 1); ////page从0开始,searchPage.getNumber()==0是第一页
        searchResult.setThisDocCount(searchPage.getNumberOfElements());
        int docIndex = 0;
        for (SearchHit<PDFDoc> hit : searchPage.getContent()) {
            int docHighlightCount = 0;
            DocHit docHit = new DocHit();
            docHit.setDocInfo(hit.getContent());
            SearchHits<PDFDocPage> pageSearchHits = (SearchHits<PDFDocPage>) hit.getInnerHits().get("pages"); //注意:key要与dao层设置的键名,entity中的键名保持一致
            DocHit.PageHit[] pageHitArr = new DocHit.PageHit[(int) pageSearchHits.getTotalHits()];
            int offset = 0;
            for (SearchHit<PDFDocPage> pageHit : pageSearchHits) {
                pageHitArr[offset] = docHit.new PageHit();
                pageHitArr[offset].setPageNumber(pageHit.getContent().getPageNumber());
                List<String> highlights = pageHit.getHighlightField("pages.content");
                pageHitArr[offset].setHighlights(highlights.toArray(new String[0]));
                pageHitArr[offset].setHighlightCount(highlights.size());
                docHighlightCount += highlights.size();
                ++offset;
            }
            docHit.setPageHits(pageHitArr);
            docHit.setHighlightCount(docHighlightCount);
            searchResult.setDocHit(docHit, docIndex);
            ++docIndex;
        }
        timer.stop();
        return searchResult;
    }

    @Override
    public Result searchInContent(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInContent(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage);
        return Result.success(result);
    }


    @Override
    public Result searchInAbstracts(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInAbstracts(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage);
        return Result.success(result);
    }

    @Override
    public Result searchInImageTexts(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInImageTexts(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage);
        return Result.success(result);
    }


}
