package cn.edu.hhu.a34backend.pojo;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class PdfImage
{
    private BufferedImage imageBuffer;

    private Rectangle imageBox;

    public PdfImage(BufferedImage imageBuffer,Rectangle imageBox)
    {
        this.imageBuffer=imageBuffer;
        this.imageBox=imageBox;
    }

}
