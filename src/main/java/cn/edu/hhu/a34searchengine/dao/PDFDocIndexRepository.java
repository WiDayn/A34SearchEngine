package cn.edu.hhu.a34searchengine.dao;

import cn.edu.hhu.a34searchengine.pojo.PDFDocPage;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PDFDocIndexRepository extends ElasticsearchRepository<PDFDocPage,String>
{
    String findByContentQStr=
            "{\"match\": {"+
                "\"content\": {"+
                    "\"query\": \"?0\","+
                    "\"analyzer\": \"ik_smart\""+
                    "}"+
                "}"+
            "}}";
    @Query(value = findByContentQStr )
    @Highlight(
           fields = @HighlightField(name="content"),
           parameters = @HighlightParameters(preTags="<em>",postTags = "</em>",fragmentSize = 100)
    )
    SearchHits<PDFDocPage> findByContent(String content);




}
