package cn.edu.hhu.a34backend.service.impl;

import cn.edu.hhu.a34backend.dao.PdfDocRepository;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.IndexService;
import cn.edu.hhu.a34backend.utils.SnowFlake;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

@Service("IndexService")
public class IndexServiceImpl implements IndexService
{
    @Value("${setting.work-id}")
    private int workerId;

    @Value("${setting.datacenter-id}")
    private int datacenterId;

    @Autowired
    public PdfDocRepository pdfDocRepository;

    @Override
    public void indexSinglePdfPage(long parentPdfUUID,int pageNumber,String singlePageText)
    {
        PdfDocPage pdfDocPage=new PdfDocPage(parentPdfUUID,pageNumber,singlePageText);
        SnowFlake flake=new SnowFlake(workerId, datacenterId, 1);
        pdfDocPage.setId(flake.nextId());
        pdfDocRepository.save(pdfDocPage);
    }

}
