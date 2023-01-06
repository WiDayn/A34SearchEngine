package cn.edu.hhu.a34searchengine.pojo;

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
