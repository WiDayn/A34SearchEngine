package cn.edu.hhu.a34searchengine.pojo;

import cn.edu.hhu.a34searchengine.dto.DocHit;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;


@Data
public class PDFDocPage
{
    @Id
    @Field(type = FieldType.Long, index = false, store = false)
    private long id;
    @Field(type = FieldType.Long, index = false, store = false)
    private long parentPdfUUID;

    @Field(type = FieldType.Long, index = false, store = false)
    private int pageNumber;

    @Field(type = FieldType.Text, index = true, store = true, analyzer = "ik_smart" , excludeFromSource = true)
    private String content;                     //高亮功能store必须为true

    @Field(type = FieldType.Object, store = true, excludeFromSource = true)
    private PDFImageText[] imageTexts;

    @Field(type = FieldType.Integer,store = false,index = false)
    private int userFeedback;               //用户反馈(点赞点踩)

    public PDFDocPage() {}

    public PDFDocPage(long parentPdfUUID, int pageNumber, String content)
    {
        this.parentPdfUUID = parentPdfUUID;
        this.pageNumber = pageNumber;
        this.content = content;
        this.id=pageNumber;
    }

}
