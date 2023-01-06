package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.pojo.PdfDocPage;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
public interface ESService
{

    void indexSinglePdfPage(long parentPdfUUID,int pageNumber,String singlePageText);

    SearchHits<PdfDocPage> queryKeywords(String keywords);
}
