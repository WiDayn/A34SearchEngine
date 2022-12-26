package cn.edu.hhu.a34backend.controller;

import cn.edu.hhu.a34backend.common.aop.LogAnnotation;
import cn.edu.hhu.a34backend.dto.SearchResult;
import cn.edu.hhu.a34backend.param.UploadParam;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.ESService;
import cn.edu.hhu.a34backend.service.UploadService;
import cn.edu.hhu.a34backend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        return uploadService.uploadPDF2(uploadParam);
    }




}
