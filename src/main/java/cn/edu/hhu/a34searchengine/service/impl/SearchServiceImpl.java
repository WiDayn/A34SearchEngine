package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PDFDocIndexDao;
import cn.edu.hhu.a34searchengine.dao.PDFDocIndexRepository;
import cn.edu.hhu.a34searchengine.vo.*;
import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.pojo.PDFDocPage;
import cn.edu.hhu.a34searchengine.service.SearchService;
import cn.edu.hhu.a34searchengine.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

@Service
public class SearchServiceImpl implements SearchService
{
    @Autowired
    public PDFDocIndexRepository pdfDocRepository;

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
        int thisDocIndex = 0;
        for (SearchHit<PDFDoc> docHit : searchPage.getContent()) {
            int docHighlightCount = 0;
            DocHit thisDocHit = new DocHit();
            thisDocHit.setDocInfo(docHit.getContent());
            SearchHits<PDFDocPage> pageHits = (SearchHits<PDFDocPage>) docHit.getInnerHits().get("pages"); //注意:key要与dao层设置的键名,entity中的键名保持一致
            Vector<DocHit.PageHit> hits=new Vector<>();
            for (SearchHit<PDFDocPage> pageHit : pageHits) {
                DocHit.PageHit hit=new DocHit.PageHit();
                hit.setPageNumber(pageHit.getContent().getPageNumber());
                List<String> highlights = pageHit.getHighlightField("pages.content");
                hit.setHighlights(highlights.toArray(new String[0]));
                hit.setHighlightCount(highlights.size());
                docHighlightCount += highlights.size();
                hits.add(hit);
            }
            thisDocHit.setPageHits(hits.toArray(new DocHit.PageHit[0]));
            thisDocHit.setHighlightCount(docHighlightCount);
            searchResult.setDocHit(thisDocHit, thisDocIndex);
            ++thisDocIndex;
        }
        timer.stop();
        return searchResult;
    }

    private SearchResult _assembleSearchResult(SearchPage<PDFDoc> searchPage,String keywords) throws IOException
    {
        Timer timer=new Timer();
        SearchResult searchResult = new SearchResult();
        searchResult.setTotalDocCount(searchPage.getTotalElements());
        searchResult.setDocHits(new DocHit[searchPage.getSize()]);
        searchResult.setTotalPageCount(searchPage.getTotalPages());
        searchResult.setThisPageNumber(searchPage.getNumber() + 1); ////page从0开始,searchPage.getNumber()==0是第一页
        searchResult.setThisDocCount(searchPage.getNumberOfElements());
        int thisDocIndex = 0;
        for (SearchHit<PDFDoc> docHit : searchPage.getContent()) {
            int docHighlightCount = 0;
            DocHit thisDocHit = new DocHit();
            PDFDoc pdfDoc=docHit.getContent();
            thisDocHit.setDocInfo(pdfDoc);
            thisDocHit.setScores(docHit.getScore());
            SearchHits<PDFDocPage> pageHits = (SearchHits<PDFDocPage>) docHit.getInnerHits().get("pages"); //注意:key要与dao层设置的键名,entity中的键名保持一致
            Vector<DocHit.PageHit> hits=new Vector<>();
            for (SearchHit<PDFDocPage> pageHit : pageHits) {
                DocHit.PageHit hit=new DocHit.PageHit();
                hit.setPageNumber(pageHit.getContent().getPageNumber());
                List<String> highlights = pageHit.getHighlightField("pages.content");
                hit.setHighlights(highlights.toArray(new String[0]));
                hit.setHighlightCount(highlights.size());
                docHighlightCount += highlights.size();
                hits.add(hit);
            }
            thisDocHit.setPageHits(hits.toArray(new DocHit.PageHit[0]));
            thisDocHit.setHighlightCount(docHighlightCount);
            searchResult.setDocHit(thisDocHit, thisDocIndex);
            ++thisDocIndex;
        }
        timer.stop();
        return searchResult;
    }

    @Override
    public Result searchInContent(String keywords, SearchCondition condition, Pageable pageRequest) throws IOException
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInContent(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage,keywords);
        return Result.success(result);
    }

    @Override
    public Result searchInAbstract(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInAbstract(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage);
        return Result.success(result);
    }

    @Override
    public Result searchInImageText(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        SearchPage<PDFDoc> searchPage = pdfDocIndexDao.searchInImageText(keywords, condition, pageRequest);
        SearchResult result = _assembleSearchResult(searchPage);
        return Result.success(result);
    }

    @Override
    public Result searchSuggest(String keywords) {
        SuggestResult suggestResult;
        try {
            suggestResult = pdfDocIndexDao.searchPhraseSuggest(keywords);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(suggestResult);
    }

}
