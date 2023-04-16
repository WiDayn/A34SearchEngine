package cn.edu.hhu.a34searchengine.pojo;

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
@Document(indexName = "pdf_doc_2")
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

    @Field(type = FieldType.Text,index = true,store = false,analyzer = "ik_smart")
    @JsonView(SearchResult.SearchResultView.class)
    protected String authors;    //作者

    @Field(type = FieldType.Text,index = true,store = true,analyzer = "ik_smart", excludeFromSource = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String articleAbstract;  //摘要, 高亮功能store必须为true

    @Field(type = FieldType.Text,index = true,store = false,analyzer = "ik_smart")
    @JsonView(SearchResult.SearchResultView.class)
    protected String keywords;   //关键字

    @Field(type = FieldType.Text,index = true,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String subset;        //期刊名/保管机构/出版社等

    @Field(type = FieldType.Long,index = false,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected Long pubDate;       //文献发表时间的时间戳

    @Field(type = FieldType.Keyword,index=true,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String genre;

    @Field(type = FieldType.Integer,index=false,store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected int type;             //文献类型枚举


    @Field(type = FieldType.Keyword,index = false, store = false)
    @JsonView(SearchResult.SearchResultView.class)
    protected String doi;

    @Field(type = FieldType.Float,index = false, store = false)
    protected float clickRate;              //文档的点击率, 每日更新一次   文档的被提取次数和被访问次数存储在mysql中

    @Field(type = FieldType.Float,index = false, store = false)
    protected float preference;             //文档的用户反馈会影响这个数值


    @Field(type = FieldType.Nested)
    protected PDFDocPage[] pages;

}

