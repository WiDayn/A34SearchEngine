package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PDFFileDao;
import cn.edu.hhu.a34searchengine.service.DocLoadService;
import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocLoadServiceImpl implements DocLoadService
{
    @Autowired
    PDFFileDao pdfFileDao;


    public Result getPDFFileDownloadURL(long pdfUUID) throws Exception
    {
        String url= pdfFileDao.getPDFFileDownloadURL("pdf",String.valueOf(pdfUUID));
        return Result.success(url);
    }

    public byte[] getPDFData(long pdfUUID) throws Exception
    {
        return pdfFileDao.getPDFData("pdf",String.valueOf(pdfUUID));
    }


}
