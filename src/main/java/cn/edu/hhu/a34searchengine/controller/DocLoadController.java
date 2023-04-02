package cn.edu.hhu.a34searchengine.controller;


import cn.edu.hhu.a34searchengine.service.DocLoadService;
import cn.edu.hhu.a34searchengine.util.HttpContextUtil;
import cn.edu.hhu.a34searchengine.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("onlineLoad")
@Slf4j
public class DocLoadController
{

    @Autowired
    DocLoadService docLoadService;

    //在线懒加载pdf
    @GetMapping("{pdfUUID}")
    public void loadOnline(@PathVariable long pdfUUID , @NotNull HttpServletResponse response, HttpServletRequest request) throws Exception
    {
        log.info("load");
        HttpContextUtil.respondDataStream(response,request,docLoadService.getPDFDataInputStream(pdfUUID));
    }


    //当前端点进去某一个pdf想查看搜索详情时,发送这个请求,这个请求会预先对pdf分页并放入缓存(如果已分页则直接忽略)
    @GetMapping("{pdfUUID}/preload")
    public Result preloadPDFByPage(@PathVariable String pdfUUID) throws Exception
    {
        docLoadService.splitPDFAndCache(Long.parseLong(pdfUUID));
        return Result.success(null);
    }



    @GetMapping("{pdfUUID}/{pageNumber}")
    public void loadOnlineByPage(@PathVariable long pdfUUID ,@PathVariable int pageNumber,@NotNull HttpServletResponse response, HttpServletRequest request)
            throws Exception
    {
        HttpContextUtil.respondDataStream(response,request,docLoadService.getPDFPageDataInputStream(pdfUUID,pageNumber));
    }


    @GetMapping("download-url/{pdfUUID}")
    private Result getDownloadURL(@PathVariable long pdfUUID) throws Exception
    {
        return docLoadService.getPDFFileDownloadURL(pdfUUID);
    }

}
