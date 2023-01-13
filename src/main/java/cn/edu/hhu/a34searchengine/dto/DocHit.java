package cn.edu.hhu.a34searchengine.dto;

import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.vo.SearchResult;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonView(SearchResult.SearchResultView.class)
public class DocHit
{


    PDFDoc docInfo;

    @Data
    @JsonView(SearchResult.SearchResultView.class)
    public class PageHit
    {
        int pageNumber;

        int highlightCount;      //这一页的总高亮数,也是highlights数组的元素个数

        String[] highlights;
    }


    private int highlightCount;    //整个文档的总高亮数


    PageHit[] pageHits;          //文档内部每页的命中情况


}
