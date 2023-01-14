package cn.edu.hhu.a34searchengine.dao;

import cn.edu.hhu.a34searchengine.pojo.PDFData;



public interface PDFCacheDao
{

    void add(long pdfUUID, PDFData data);

    PDFData get(long pdfUUID);

    boolean isAvailable(long pdfUUID);

}
