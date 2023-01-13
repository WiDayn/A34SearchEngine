package cn.edu.hhu.a34searchengine.pojo;

import cn.edu.hhu.a34searchengine.dto.Point;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class PDFImageText
{
    @Field(type = FieldType.Text, index = true, store = false, analyzer = "ik_smart")
    String text;

    @Field(type = FieldType.Float, index = false, store = false)
    Point[] points;
}
