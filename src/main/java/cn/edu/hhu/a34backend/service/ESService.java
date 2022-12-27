package cn.edu.hhu.a34backend.service;

import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface ESService
{

    void indexSinglePdfPage(long parentPdfUUID,int pageNumber,String singlePageText);

    SearchHits<PdfDocPage> queryKeywords(String keywords);
}
