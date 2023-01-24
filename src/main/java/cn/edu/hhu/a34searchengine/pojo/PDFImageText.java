package cn.edu.hhu.a34searchengine.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class PDFImageText
{

    @Field(type = FieldType.Integer, index = false, store = false)
    private int pageNumber;

    @Field(type = FieldType.Text, index = true, store = true, analyzer = "ik_smart")
    String text;


    @Field(type = FieldType.Float,index = false)
    float[] baseCoordinate;

    @Field(type = FieldType.Float, index = false)
    float[][] points;


}
