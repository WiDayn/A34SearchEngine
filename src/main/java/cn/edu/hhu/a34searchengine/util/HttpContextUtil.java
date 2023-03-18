package cn.edu.hhu.a34searchengine.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class HttpContextUtil
{
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static void  respondDataStream(HttpServletResponse response, HttpServletRequest request,InputStream inputStream)
            throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        byte[] data= bis.readAllBytes();
        // 下载的字节范围
        int startByte, endByte, totalByte;
        if (request != null && request.getHeader("range") != null) {
            // 断点续传
            String[] range = request.getHeader("range").replaceAll("[^0-9\\-]", "").split("-");
            // 文件总大小
            totalByte = data.length;
           // totalByte = bis.available();
            // 下载起始位置
            startByte = Integer.parseInt(range[0]);
            // 下载结束位置
            if (range.length > 1) {
                endByte = Integer.parseInt(range[1]);
            } else {
                endByte = totalByte - 1;
            }
            // 返回http状态
            response.setStatus(206);
        } else {
            // 正常下载
            // 文件总大小
            totalByte = data.length;
        //    totalByte = bis.available();
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

        //bis.skip(startByte);

        bos.write(data, startByte, length);
        /*
        while ((len = bis.read(buff, 0, buff.length)) != -1) {
            if (length <= len) {
                bos.write(buff, 0, length);
                break;
            } else {
                length -= len;
                bos.write(buff, 0, len);
            }
        }*/
    }

}
