package cn.edu.hhu.a34searchengine.controller;

import cn.edu.hhu.a34searchengine.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("document")
public class DocDetailController
{

    //获取文档的信息
    @GetMapping("{pdfUUID}/detail")
    public Result getDocDetail(@PathVariable long pdfUUID)
    {
        return null;
    }

}
