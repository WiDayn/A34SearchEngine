package cn.edu.hhu.a34backend.service.impl;

import cn.edu.hhu.a34backend.param.UploadParam;
import cn.edu.hhu.a34backend.service.UploadService;
import cn.edu.hhu.a34backend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    @Override
    public Result uploadPDF(UploadParam uploadParam) {

        System.out.println(uploadParam.getData());
        return null;
    }
}
