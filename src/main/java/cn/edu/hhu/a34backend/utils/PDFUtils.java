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

    //输出分割的pdf到String[]数组
    public static String[] split(String originFile, String outputPrefix, String outputPath) throws IOException {
        PDDocument pdDocument = PDDocument.load(new File(originFile));
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(pdDocument);
        Iterator<PDDocument> iterator = pages.listIterator();
        int pagesCount = 0;

        String[] pdfPagesText=new String[pages.size()];

        PDFTextStripper pdfStripper=new PDFTextStripper();
        System.out.println("ok2");
        while(iterator.hasNext())
        {
            PDDocument pd = iterator.next();
            pdfPagesText[pagesCount++]=pdfStripper.getText(pd);
            System.out.println("ok"+pagesCount);
            pd.close();
        }
        pdDocument.close();
        return pdfPagesText;
    }
}
