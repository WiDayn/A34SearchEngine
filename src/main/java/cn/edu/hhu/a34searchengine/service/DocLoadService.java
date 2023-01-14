package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.vo.Result;

import java.io.InputStream;

public interface DocLoadService
{
    Result getPDFFileDownloadURL(long pdfUUID) throws Exception;


    void splitPDFAndCache(long pdfUUID) throws Exception;

    InputStream getPDFDataInputStream(long pdfUUID) throws Exception;

    InputStream getPDFPageDataInputStream(long pdfUUID, int pageNumber) throws Exception;

}
