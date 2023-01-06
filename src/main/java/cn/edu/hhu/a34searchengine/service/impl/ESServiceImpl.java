package cn.edu.hhu.a34searchengine.service.impl;

import cn.edu.hhu.a34searchengine.dao.PdfDocRepository;
import cn.edu.hhu.a34searchengine.pojo.PdfDocPage;
import cn.edu.hhu.a34searchengine.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("ESService")
public class ESServiceImpl implements ESService
{
    @Autowired
    public PdfDocRepository pdfDocRepository;

    @Override
    @Async
    public void indexSinglePdfPage(long parentPdfUUID,int pageNumber,String singlePageText)
    {
        PdfDocPage pdfDocPage=new PdfDocPage(parentPdfUUID,pageNumber,singlePageText,null);

        pdfDocRepository.save(pdfDocPage);
    }

    @Override
    public SearchHits<PdfDocPage> queryKeywords(String keywords)
    {
        return pdfDocRepository.findByContent(keywords);
    }


}
