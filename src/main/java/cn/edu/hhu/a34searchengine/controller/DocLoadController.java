package cn.edu.hhu.a34searchengine.controller;


import cn.edu.hhu.a34searchengine.service.DocLoadService;
import cn.edu.hhu.a34searchengine.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping("document")
public class DocLoadController
{

    @Autowired
    DocLoadService docLoadService;

    //在线懒加载pdf
    @GetMapping("{pdfUUID}")
    public void loadOnline(@PathVariable long pdfUUID , @NotNull HttpServletResponse response, HttpServletRequest request) throws Exception
    {
        BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(docLoadService.getPDFData(pdfUUID)));
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        // 下载的字节范围
        int startByte, endByte, totalByte;
        if (request != null && request.getHeader("range") != null)
        {
            // 断点续传
            String[] range = request.getHeader("range").replaceAll("[^0-9\\-]", "").split("-");
            // 文件总大小
            totalByte = bis.available();
            // 下载起始位置
            startByte = Integer.parseInt(range[0]);
            // 下载结束位置
            if (range.length > 1)
            {
                endByte = Integer.parseInt(range[1]);
            }
            else
            {
                endByte = totalByte - 1;
            }
            // 返回http状态
            response.setStatus(206);
        }
        else
        {
            // 正常下载
            // 文件总大小
            totalByte = bis.available();
            // 下载起始位置
            startByte = 0;
            // 下载结束位置
            endByte = totalByte - 1;
            // 返回http状态
            response.setHeader("Accept-Ranges", "bytes");
            response.setStatus(200);
        }
        // 需要下载字节数
        int length = endByte - startByte + 1;
        // 响应头
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + totalByte);
//            response.setContentType("application/pdf");
        response.setContentType("application/octet-stream");
        response.setContentLength(length);
        // 响应内容
        bis.skip(startByte);
        int len = 0;
        byte[] buff = new byte[1024 * 64];
        while ((len = bis.read(buff, 0, buff.length)) != -1)
        {
            if (length <= len)
            {
                bos.write(buff, 0, length);
                break;
            }
            else
            {
                length -= len;
                bos.write(buff, 0, len);
            }
        }
    }



    @GetMapping("{pdfUUID}/{pageNumber}")
    public void loadOnlineByPage(@PathVariable long pdfUUID ,@PathVariable int pageNumber,@NotNull HttpServletResponse response, HttpServletRequest request)
            throws Exception
    {
        BufferedInputStream bis=new BufferedInputStream(new ByteArrayInputStream(docLoadService.getPDFData(pdfUUID)));
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        // 下载的字节范围
        int startByte, endByte, totalByte;
        if (request != null && request.getHeader("range") != null)
        {
            // 断点续传
            String[] range = request.getHeader("range").replaceAll("[^0-9\\-]", "").split("-");
            // 文件总大小
            totalByte = bis.available();
            // 下载起始位置
            startByte = Integer.parseInt(range[0]);
            // 下载结束位置
            if (range.length > 1)
            {
                endByte = Integer.parseInt(range[1]);
            }
            else
            {
                endByte = totalByte - 1;
            }
            // 返回http状态
            response.setStatus(206);
        }
        else
        {
            // 正常下载
            // 文件总大小
            totalByte = bis.available();
            // 下载起始位置
            startByte = 0;
            // 下载结束位置
            endByte = totalByte - 1;
            // 返回http状态
            response.setHeader("Accept-Ranges", "bytes");
            response.setStatus(200);
        }
        // 需要下载字节数
        int length = endByte - startByte + 1;
        // 响应头
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + totalByte);
//            response.setContentType("application/pdf");
        response.setContentType("application/octet-stream");
        response.setContentLength(length);
        // 响应内容
        bis.skip(startByte);
        int len = 0;
        byte[] buff = new byte[1024 * 64];

        while ((len = bis.read(buff, 0, buff.length)) != -1)
        {
            if (length <= len)
            {
                bos.write(buff, 0, length);
                break;
            }
            else
            {
                length -= len;
                bos.write(buff, 0, len);
            }
        }
    }


    @GetMapping("download-url/{pdfUUID}")
    private Result getDownloadURL(@PathVariable long pdfUUID) throws Exception
    {
        return docLoadService.getPDFFileDownloadURL(pdfUUID);
    }

}
