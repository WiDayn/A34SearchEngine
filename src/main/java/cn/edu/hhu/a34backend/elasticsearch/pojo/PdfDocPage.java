package cn.edu.hhu.a34backend.elasticsearch.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Document(indexName="PdfDoc",type="")
public class PdfDocPage
{
    private long id;


    private String parentMD5;
    private int pageNumber;
    private String content;


}
