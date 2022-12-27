package cn.edu.hhu.a34backend.utils;

import cn.edu.hhu.a34backend.pojo.PdfContent;
import cn.edu.hhu.a34backend.pojo.PdfImage;
import cn.edu.hhu.a34backend.pojo.Rectangle;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PDFUtils
{

    /**
     * 切分PDF文件并返回页数，下标从1开始
     * <p>
     * originFile 源文件路径
     * outputPrefix 切分后文件的前缀
     * outputPath 切分后文件存放的路径
     * <p>
     * pagesCount 切分后页数
     */
    public static int divide(String originFile, String outputPrefix, String outputPath) throws IOException
    {
        PDDocument pdDocument = PDDocument.load(new File(originFile));

        Splitter splitter = new Splitter();

        List<PDDocument> Pages = splitter.split(pdDocument);
        Iterator<PDDocument> iterator = Pages.listIterator();
        int pagesCount = 0;
        while (iterator.hasNext())
        {
            PDDocument pd = iterator.next();
            pd.save(outputPath + outputPrefix + "-out-" + ++pagesCount + ".pdf");
        }
        pdDocument.close();

        return pagesCount;
    }

    //以页为单位分割,输出分割的pdf文字到String[]数组
    public static PdfContent[] split(byte[] pdfBinData) throws IOException
    {
        PDDocument pdDocument = PDDocument.load(pdfBinData);
        Splitter splitter = new Splitter();
        List<PDDocument> pages = splitter.split(pdDocument);
        PdfContent[] pdfPages = new PdfContent[pages.size()];
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int offset = 0;

        for (PDDocument page : pages)
        {
            Vector<PdfImage> images = getSinglePageImages(page);
            pdfPages[offset++] = new PdfContent(pdfStripper.getText(page), images);
            for (PdfImage image : images)
            {
                if (image != null)
                {
                    System.out.println(image.getImageBox());
                }
            }
            page.close();
            System.out.println("ok" + offset);
        }
        pdDocument.close();
        return pdfPages;
    }

    private static Vector<PdfImage> getSinglePageImages(PDDocument singlePageDoc) throws IOException
    {
        PDPage page = singlePageDoc.getPage(0);
        Vector<PdfImage> images = new Vector<>();
        PDFStreamEngine pdfStreamEngine = new PDFStreamEngine()
        {
            @Override
            protected void processOperator(Operator operator, List<COSBase> operands) throws IOException
            {
                addOperator(new DrawObject());
                addOperator(new SetGraphicsStateParameters());
                addOperator(new Save());
                addOperator(new Restore());
                addOperator(new SetMatrix());
                addOperator(new Concatenate());
                String operation = operator.getName();
                if ("Do".equals(operation))
                {
                    COSName objectName = (COSName) operands.get(0);
                    PDXObject xobject = getResources().getXObject(objectName);
                    if (xobject instanceof PDImageXObject)
                    {
                        PDImageXObject imageObj = (PDImageXObject) xobject;
                        BufferedImage bImage = imageObj.getImage();
                        Matrix imgMatrix = getGraphicsState().getCurrentTransformationMatrix();
                        float imgXScale = imgMatrix.getScalingFactorX();
                        float imgYScale = imgMatrix.getScalingFactorY();

                        float imgPosX = imgMatrix.getTranslateX();
                        float imgPosY = imgMatrix.getTranslateY();
                        images.add(new PdfImage(bImage, new Rectangle(imgPosX, imgPosY, imgXScale, imgYScale)));
                    }
                    else if (xobject instanceof PDFormXObject)
                    {
                        PDFormXObject form = (PDFormXObject) xobject;
                        showForm(form);
                    }
                }
                else
                {
                    super.processOperator(operator, operands);
                }

            }
        };

        pdfStreamEngine.processPage(page);
        return images;
    }

}
