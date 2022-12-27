package cn.edu.hhu.a34backend.pojo;

import lombok.Data;

import java.util.Vector;

@Data
public class PdfContent
{

    Vector<PdfImage> images;

    private String text;

    public PdfContent(String text,Vector<PdfImage> images)
    {
        this.images = images;
        this.text = text;
    }
}
