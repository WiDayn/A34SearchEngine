package cn.edu.hhu.a34searchengine.service;

import cn.edu.hhu.a34searchengine.param.UploadParam;
import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface UploadService {

    Result uploadPDF(UploadParam uploadParam);

    //测试用
    Result uploadPDF2(UploadParam uploadParam) throws IOException;
}
