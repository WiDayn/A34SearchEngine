package cn.edu.hhu.a34searchengine.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonView(SearchResult.SearchResultView.class)
public class SearchResult
{
    private long totalDocCount;             //总命中的文档数,包括其他页的

    private int totalPageCount;              //页数

    private int thisPageNumber;               //当前页的页号

    private int thisDocCount;               //当前页的文档个数

    DocHit[] docHits;                   //每个文档信息和高亮情况

    public interface SearchResultView extends Result.ResultView {}

    public void setDocHit(DocHit docHit,int index)
    {
        docHits[index]=docHit;
    }

}
