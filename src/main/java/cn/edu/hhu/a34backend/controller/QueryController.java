package cn.edu.hhu.a34backend.controller;

import cn.edu.hhu.a34backend.dto.SearchResult;
import cn.edu.hhu.a34backend.pojo.PdfDocPage;
import cn.edu.hhu.a34backend.service.ESService;
import cn.edu.hhu.a34backend.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("query")
public class QueryController
{

    private ESService esService;

    @Value("${test.test-pdf}")
    private String testPDF;

    @GetMapping("keywords")
    public Result queryKeywords(String queryString)
    {
        SearchHits<PdfDocPage> hitPages=esService.queryKeywords(queryString);
        List<SearchResult> searchResults=new ArrayList<>();
        for(SearchHit<PdfDocPage> hitPage: hitPages)
        {
            List<String> hitStrings=hitPage.getHighlightField("content");
            int pageNumber=hitPage.getContent().getPageNumber();
            long parentPdfUUID=hitPage.getContent().getParentPdfUUID();

            SearchResult searchResult=new SearchResult(hitStrings,pageNumber,parentPdfUUID);

            searchResults.add(searchResult);
        }
        return Result.success(searchResults,"成功");
    }

    @GetMapping("pdf")
    public void loadPDFByPage(HttpServletResponse response, HttpServletRequest request) throws IOException
    {
        System.out.println("called");
        BufferedInputStream bis = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        InputStream is = null;
        File file= new File("F:\\static\\3d.pdf");
        is = new FileInputStream(file);
        bis = new BufferedInputStream(is);
        os = response.getOutputStream();
        bos = new BufferedOutputStream(os);
        // 下载的字节范围
        int startByte, endByte, totalByte;
        if (request != null && request.getHeader("range") != null)
        {
            // 断点续传
            String[] range = request.getHeader("range").replaceAll("[^0-9\\-]", "").split("-");
            // 文件总大小
            totalByte = is.available();
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
            totalByte = is.available();
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


}
