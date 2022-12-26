package cn.edu.hhu.a34backend.service;

import cn.edu.hhu.a34backend.param.IndexPdfParam;
import org.springframework.stereotype.Service;

@Service
public interface IndexService
{
    void indexSinglePdfPage(long parentPdfUUID,int pageNumber,String singlePageText);

}
