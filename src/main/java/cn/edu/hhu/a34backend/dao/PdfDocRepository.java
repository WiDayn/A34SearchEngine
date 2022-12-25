package cn.edu.hhu.a34backend.dao;

import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PdfDocRepository extends ElasticsearchRepository<PdfDocPage,Long>
{

    List<PdfDocPage> findByContent(String content);
}
