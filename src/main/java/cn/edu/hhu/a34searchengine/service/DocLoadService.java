package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.vo.Result;

public interface DocLoadService
{
    Result getPDFFileDownloadURL(long pdfUUID) throws Exception;

    byte[] getPDFData(long pdfUUID) throws Exception;


}
