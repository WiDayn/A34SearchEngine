package cn.edu.hhu.a34searchengine.dao.impl;

import cn.edu.hhu.a34searchengine.dao.PDFFileDao;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Repository
public class PDFFileDaoImpl implements PDFFileDao
{

    @Autowired
    MinioClient minioClient;

    @Override
    public byte[] getPDFData(String bucketName, String pdfFileName)
            throws Exception
    {
        GetObjectResponse response=minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(pdfFileName)
                        .build()
        );
        byte[] bytes=response.readAllBytes();
        response.close();
        return bytes;
    }

    @Override
    public InputStream getPDFInputStream(String bucketName, String pdfFileName)
            throws Exception
    {
        GetObjectResponse response=minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(pdfFileName)
                        .build()
        );
        return response;
    }


    @Override
    public String getPDFFileDownloadURL(String bucketName, String pdfFileName)
            throws Exception
    {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .method(Method.GET)
                        .object(pdfFileName)
                        .expiry(60, TimeUnit.MINUTES)
                        .build()
        );
    }
}
