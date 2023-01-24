package cn.edu.hhu.a34searchengine.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Data
public class PDFDocPage
{
    @Field(type = FieldType.Integer, index = false, store = false)
    private int pageNumber;

    @Field(type = FieldType.Text, index = true, store = true, analyzer = "ik_smart")
    private String content;                     //高亮功能store必须为true

    @Field(type = FieldType.Nested)
    protected PDFImageText[] imageTexts;
    public PDFDocPage() {}

    public PDFDocPage(long parentPdfUUID, int pageNumber, String content)
    {
        this.pageNumber = pageNumber;
        this.content = content;
    }

}
