package cn.edu.hhu.a34searchengine.pojo;

import cn.edu.hhu.a34searchengine.dto.DocHit;
import cn.edu.hhu.a34searchengine.vo.SearchResult;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "pdf_doc")
public class PDFDoc
{
    @Id
    @Field(type = FieldType.Long, index = false, store = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonView(SearchResult.SearchResultView.class)
    protected Long id;  //es中id为string,long转string会发生精度丢失(javascript中number类型为浮点数),使用serializer

    @Field(type = FieldType.Text,index = true,store = false,analyzer = "ik_smart")
    @JsonView(SearchResult.SearchResultView.class)
    protected String title;       //标题

    @Field(type = FieldType.Keyword,index = false,store = false,analyzer = "ik_smart")
    @JsonView(SearchResult.SearchResultView.class)
    protected String authors;    //作者

    @Field(type = FieldType.Text,index = true,store = true,analyzer = "ik_smart", excludeFromSource = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String articleAbstract;  //摘要, 高亮功能store必须为true

    @Field(type = FieldType.Text,index = true,store = false,analyzer = "ik_smart")
    @JsonView(SearchResult.SearchResultView.class)
    protected String keywords;   //关键字

    @Field(type = FieldType.Keyword,index = false,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String subset;        //期刊名/保管机构/出版社等

    @Field(type = FieldType.Long,index = false,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected Long pubDate;       //文献发表时间的时间戳

    @Field(type = FieldType.Keyword,index=false,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String genre;

    @Field(type = FieldType.Keyword,index = false, store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String doi;
    @Field(type = FieldType.Nested)
    protected PDFDocPage[] pages;


    public PDFDoc()
    {

    }

}

