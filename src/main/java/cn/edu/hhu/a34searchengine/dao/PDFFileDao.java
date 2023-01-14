package cn.edu.hhu.a34searchengine.dao;

import java.io.InputStream;

public interface PDFFileDao
{

    byte[] getPDFData(String bucketName, String pdfFileName)
            throws Exception;

    InputStream getPDFInputStream(String bucketName, String pdfFileName)
            throws Exception;


    String getPDFFileDownloadURL(String bucketName, String pdfFileName)
            throws Exception;

}
