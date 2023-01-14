package cn.edu.hhu.a34searchengine.util;

import cn.edu.hhu.a34searchengine.pojo.PDFData;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public class PDFUtil
{


    public static Vector<byte[]> split(InputStream pdfInputStream) throws IOException
    {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedOutputStream boutput = new BufferedOutputStream(output);
        PdfOutputStream pdfout = new PdfOutputStream(boutput);
        List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc)
        {
            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange)
            {
                return new PdfWriter(pdfout);
            }
        }.splitByPageCount(1);
        Vector<byte[]> pagesData=new Vector<>();
        for (PdfDocument doc : splitDocuments) {
            doc.close();
            pagesData.add(output.toByteArray());
        }
        return pagesData;
    }
}
