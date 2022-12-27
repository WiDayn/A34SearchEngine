package cn.edu.hhu.a34backend.utils;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class PDFUtils
{

    /**
     * 切分PDF文件并返回页数，下标从1开始
     *
     * originFile 源文件路径
     * outputPrefix 切分后文件的前缀
     * outputPath 切分后文件存放的路径
     *
     * pagesCount 切分后页数
     */
    public static int divide(String originFile, String outputPrefix, String outputPath) throws IOException {
        PDDocument pdDocument = PDDocument.load(new File(originFile));

        Splitter splitter = new Splitter();

        List<PDDocument> Pages = splitter.split(pdDocument);
        Iterator<PDDocument> iterator = Pages.listIterator();
        int pagesCount = 0;
        while(iterator.hasNext()) {
            PDDocument pd = iterator.next();
            pd.save(outputPath+ outputPrefix + "-out-" + ++pagesCount +".pdf");
        }
        pdDocument.close();

        return pagesCount;
    }

    //以页为单位分割,输出分割的pdf文字到String[]数组
    public static String[] split(byte[] pdfBinData) throws IOException
    {
        PDDocument pdDocument = PDDocument.load(pdfBinData);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(pdDocument);
        String[] pdfPagesText = new String[pages.size()];
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int pagesCount = 0;
        for (PDDocument page : pages)
        {
            pdfPagesText[pagesCount++] = pdfStripper.getText(page);
            System.out.println("ok" + pagesCount);
            page.close();
        }
        pdDocument.close();
        return pdfPagesText;
    }
}
