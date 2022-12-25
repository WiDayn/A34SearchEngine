package cn.edu.hhu.a34backend.controller;

import cn.edu.hhu.a34backend.common.aop.LogAnnotation;
import cn.edu.hhu.a34backend.param.UploadParam;
import cn.edu.hhu.a34backend.service.UploadService;
import cn.edu.hhu.a34backend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("pdf")
    @LogAnnotation(module = "上传",operation = "PDF")
    public Result PDF(@RequestBody UploadParam uploadParam){
        return uploadService.uploadPDF(uploadParam);
    }
}
