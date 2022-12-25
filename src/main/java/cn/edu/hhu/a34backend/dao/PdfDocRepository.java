package cn.edu.hhu.a34backend.dao;

import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PdfDocRepository extends ElasticsearchRepository<PdfDocPage,Long>
{
}
