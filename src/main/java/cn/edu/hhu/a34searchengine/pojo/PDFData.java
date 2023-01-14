package cn.edu.hhu.a34searchengine.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.Vector;

@Data
public class PDFData implements Serializable
{
    @Id
    private Long pdfUUID;

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class PDFPageData implements Serializable
    {
        @Id
        private int pageNumber;
        private byte[] data;

    }
    Vector<PDFPageData> pages;
    public PDFData()
    {
        pages=new Vector<>();
    }

    public void addPageData(int pageNumber,byte[] data)
    {
        PDFPageData pageData=new PDFPageData(pageNumber,data);
        pages.add(pageData);
    }

}
