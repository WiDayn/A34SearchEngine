package cn.edu.hhu.a34backend.service.impl;

import cn.edu.hhu.a34backend.dao.PdfDocRepository;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl implements IndexService
{

    @Autowired
    public PdfDocRepository pdfDocRepository;

    @Override
    public void indexSinglePdfPage(long parentPdfUuid,int pageNumber,String singlePageBase64Str)
    {
        PdfDocPage pdfDocPage=new PdfDocPage(parentPdfUuid,pageNumber,singlePageBase64Str);
        pdfDocRepository.save(pdfDocPage);
    }

}
