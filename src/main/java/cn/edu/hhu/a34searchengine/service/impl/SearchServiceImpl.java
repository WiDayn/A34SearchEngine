package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PDFDocIndexDao;
import cn.edu.hhu.a34searchengine.pojo.PDFImageText;
import cn.edu.hhu.a34searchengine.vo.DocHit;
import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.pojo.PDFDocPage;
import cn.edu.hhu.a34searchengine.service.SearchService;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.Result;
import cn.edu.hhu.a34searchengine.vo.SearchResult;
import com.google.errorprone.annotations.DoNotCall;
import org.omg.CORBA.IntHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class SearchServiceImpl implements SearchService
{

    @Autowired
    public PDFDocIndexDao pdfDocIndexDao;

    private DocHit.ContentHit[] _assembleContentHits(SearchHit<PDFDoc> docHit, int[] highlightCountRef)
    {
        SearchHits<PDFDocPage> contentHits = (SearchHits<PDFDocPage>) docHit.getInnerHits().get("pages"); //注意:key要与dao层设置的键名,entity中的键名保持一致
        if(contentHits==null) return null;
        DocHit.ContentHit[] contentHitsArr = new DocHit.ContentHit[contentHits.getSearchHits().size()];
        int i=0;
        for (SearchHit<PDFDocPage> contentHit : contentHits) {
            DocHit.ContentHit hit = new DocHit.ContentHit();
            hit.setPageNumber(contentHit.getContent().getPageNumber());
            List<String> highlights = contentHit.getHighlightField("pages.content");
            hit.setHighlights(highlights.toArray(new String[0]));
            hit.setHighlightCount(highlights.size());
            highlightCountRef[0] += highlights.size();
            contentHitsArr[i++]=hit;
        }
        return contentHitsArr;
    }

    private DocHit.ImageTextHit[] _assembleImageTextHits(SearchHit<PDFDoc> docHit, int[] highlightCountRef)
    {
        SearchHits<PDFImageText> imageTextHits = (SearchHits<PDFImageText>) docHit.getInnerHits().get("pages.imageTexts"); //注意:key要与dao层设置的键名,entity中的键名保持一致
        if(imageTextHits==null) return null;
        Map<Integer, Vector<DocHit.ImageTextHit.Elem>> imageTextHitMap=new HashMap<>();
        for (SearchHit<PDFImageText> imageTextHit : imageTextHits) {
            DocHit.ImageTextHit.Elem hit=new DocHit.ImageTextHit.Elem();
            int pageNumber=imageTextHit.getContent().getPageNumber();
            hit.setBaseCoordinate(imageTextHit.getContent().getBaseCoordinate());
            hit.setPoints(imageTextHit.getContent().getPoints());
            List<String> highlights = imageTextHit.getHighlightField("pages.imageTexts.text");
            hit.setHighlights(highlights.toArray(new String[0]));
            highlightCountRef[0] += highlights.size();
            imageTextHitMap.compute(pageNumber,(k,v)->{
                if(v==null) v=new Vector<>();
                v.add(hit);
                return v;
            });
        }
        DocHit.ImageTextHit[] imageTextHitArr=new DocHit.ImageTextHit[imageTextHitMap.size()];
        int offset=0;
        for(Map.Entry<Integer,Vector<DocHit.ImageTextHit.Elem>> entry : imageTextHitMap.entrySet()){
            imageTextHitArr[offset]=new DocHit.ImageTextHit();
            imageTextHitArr[offset].setPageNumber(entry.getKey());
            imageTextHitArr[offset].setHighlightElems(entry.getValue().toArray(new DocHit.ImageTextHit.Elem[0]));
            ++offset;
        }
        return imageTextHitArr;
    }

    private DocHit.AbstractHit _assembleAbstractHit(SearchHit<PDFDoc> docHit, int[] highlightCountRef)
    {
        List<String> abstractHits = docHit.getHighlightField("articleAbstract"); //注意:key要与dao层设置的键名,entity中的键名保持一致
        DocHit.AbstractHit abstractHit=new DocHit.AbstractHit();
        abstractHit.setHighlights(abstractHits.toArray(new String[0]));
        highlightCountRef[0]=abstractHits.size();
        abstractHit.setHighlightCount(highlightCountRef[0]);
        return abstractHit;
    }

    private SearchResult _generateBasicResult(SearchPage<PDFDoc> searchPage)
    {
        SearchResult searchResult = new SearchResult();
        searchResult.setTotalDocCount(searchPage.getTotalElements());
        searchResult.setDocHits(new DocHit[searchPage.getContent().size()]);
        searchResult.setTotalPageCount(searchPage.getTotalPages());
        searchResult.setThisPageNumber(searchPage.getNumber() + 1); ////page从0开始,searchPage.getNumber()==0是第一页
        searchResult.setThisDocCount(searchPage.getNumberOfElements());
        return searchResult;
    }

    //将搜索结果装配到SearchResult
    private SearchResult _assembleSearchResult(SearchPage<PDFDoc> searchPage)
    {
        Timer timer=new Timer();
        SearchResult searchResult = _generateBasicResult(searchPage);
        int thisDocIndex = 0;
        for (SearchHit<PDFDoc> docHit : searchPage.getContent()) {
            DocHit thisDocHit = new DocHit();
            thisDocHit.setDocInfo(docHit.getContent());
            int[] contentHighlightCount = new int[1];
            DocHit.ContentHit[] contentHitsArr = _assembleContentHits(docHit,contentHighlightCount);
            int[] imageTextHighlightCount = new int[1];
            DocHit.ImageTextHit[] imageTextHitArr=_assembleImageTextHits(docHit,imageTextHighlightCount);
            int[] abstractHighlightCount=new int[1];
            DocHit.AbstractHit abstractHit=_assembleAbstractHit(docHit,abstractHighlightCount);
            thisDocHit.setContentHits(contentHitsArr);
            thisDocHit.setImageTextHits(imageTextHitArr);
            thisDocHit.setAbstractHit(abstractHit);
            thisDocHit.setHighlightCount(contentHighlightCount[0]+imageTextHighlightCount[0]);
            thisDocHit.setScores(docHit.getScore());
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
        SearchResult result = _assembleSearchResult(searchPage);
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

}
