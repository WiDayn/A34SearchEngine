package cn.edu.hhu.a34backend.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Document(indexName = "pdfdoc")
public class PdfDocPage
{
    @Field(type=FieldType.Long,index=false)
    private long parentPdfUuid;

    @Field(type=FieldType.Long,index=false)
    private int pageNumber;

    @Field(type=FieldType.Text,index=true,analyzer = "ik_smart")
    private String content;

    public PdfDocPage(){}

    public PdfDocPage(long parentPdfUuid,int pageNumber,String content)
    {
        this.parentPdfUuid=parentPdfUuid;
        this.pageNumber=pageNumber;
        this.content=content;
    }



}
