package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PDFCacheDao;
import cn.edu.hhu.a34searchengine.dao.PDFFileDao;
import cn.edu.hhu.a34searchengine.pojo.PDFData;
import cn.edu.hhu.a34searchengine.service.DocLoadService;
import cn.edu.hhu.a34searchengine.util.FileUtil;
import cn.edu.hhu.a34searchengine.util.PDFUtil;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Vector;

@Service
public class DocLoadServiceImpl implements DocLoadService
{
    @Autowired
    PDFFileDao pdfFileDao;

    @Autowired
    PDFCacheDao pdfCacheDao;


    @Override
    public Result getPDFFileDownloadURL(long pdfUUID) throws Exception
    {
        String url= pdfFileDao.getPDFFileDownloadURL("pdf",String.valueOf(pdfUUID));
        return Result.success(url);
    }


    @Override
    public void splitPDFAndCache(long pdfUUID) throws Exception
    {
        Timer timer = new Timer();
        if (pdfCacheDao.isAvailable(pdfUUID))
            return;
        Vector<byte[]> pagesData = PDFUtil.split(pdfFileDao.getPDFInputStream("pdf", String.valueOf(pdfUUID)));
        PDFData pdfData = new PDFData();
        FileUtil.createAndSave("D:\\tmp.pdf",pagesData.get(0));
        pdfData.setPdfUUID(pdfUUID);
        int pageNumber = 1;
        for (byte[] pageData : pagesData) {
            pdfData.addPageData(pageNumber++, pageData);
        }
        pdfCacheDao.add(pdfUUID, pdfData);
        timer.stop();
    }

    @Override
    public InputStream getPDFDataInputStream(long pdfUUID) throws Exception
    {
        return pdfFileDao.getPDFInputStream("pdf",String.valueOf(pdfUUID));
    }



    @Override
    public InputStream getPDFPageDataInputStream(long pdfUUID, int pageNumber) throws Exception
    {
        Timer timer=new Timer();
        PDFData data = pdfCacheDao.get(pdfUUID);
        if(data==null){
            splitPDFAndCache(pdfUUID);
            data=pdfCacheDao.get(pdfUUID);
        }
        timer.stop();
        return new ByteArrayInputStream(data.getPages().get(pageNumber-1).getData());
    }


}
