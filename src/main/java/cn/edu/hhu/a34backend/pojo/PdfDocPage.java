package cn.edu.hhu.a34backend.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Document(indexName = "pdfdoc")
public class PdfDocPage
{
    @Id
    @Field(type=FieldType.Keyword,index=false,store = true)
    private String id;
    @Field(type=FieldType.Long,index=false,store = true)
    private long parentPdfUUID;

    @Field(type=FieldType.Long,index=false,store = true)
    private int pageNumber;

    @Field(type=FieldType.Text,index=true,store = true,analyzer = "ik_smart")
    private String content;

    public PdfDocPage(){}

    public PdfDocPage(long parentPdfUUID,int pageNumber,String content)
    {
        this.parentPdfUUID=parentPdfUUID;
        this.pageNumber=pageNumber;
        this.content=content;
    }



}
