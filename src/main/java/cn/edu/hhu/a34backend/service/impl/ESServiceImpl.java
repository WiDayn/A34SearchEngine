package cn.edu.hhu.a34backend.service.impl;

import cn.edu.hhu.a34backend.dao.PdfDocRepository;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.ESService;
import cn.edu.hhu.a34backend.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ESService")
public class ESServiceImpl implements ESService
{
    @Value("${setting.work-id}")
    private int workerId;

    @Value("${setting.datacenter-id}")
    private int datacenterId;

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
