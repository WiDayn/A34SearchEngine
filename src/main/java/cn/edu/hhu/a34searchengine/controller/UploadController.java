package cn.edu.hhu.a34searchengine.controller;

import cn.edu.hhu.a34searchengine.common.aop.LogAnnotation;
import cn.edu.hhu.a34searchengine.param.UploadParam;

import cn.edu.hhu.a34searchengine.service.UploadService;
import cn.edu.hhu.a34searchengine.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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


    //测试用,测试上传&添加到index功能
    @PostMapping("pdf2")
    public Result PDF2(@RequestBody UploadParam uploadParam) throws IOException
    {
        long start=System.currentTimeMillis();
        Result result=uploadService.uploadPDF2(uploadParam);
        System.out.println("===用时==="+(System.currentTimeMillis()-start));
        return result;
    }




}
