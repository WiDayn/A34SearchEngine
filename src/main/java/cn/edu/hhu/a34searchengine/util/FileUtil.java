package cn.edu.hhu.a34searchengine.util;

import java.io.*;

public class FileUtil
{
    private File file;


    FileUtil(String filePath)
    {
        file=new File(filePath);
    }


    public void openFile(String filePath)
    {
        file = new File(filePath);
    }

    public void writeFile(byte[] bytes) throws IOException
    {
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(bytes);
        fop.flush();
        fop.close();
    }

    public static void createAndSave(String filePath,byte[] bytes) throws IOException
    {
        File file = new File(filePath);
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(bytes);
        fop.flush();
        fop.close();
    }

    public static byte[] openAndRead(String filePath) throws IOException
    {
        File file = new File(filePath);
        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(fin);
        byte[] bytes=bin.readAllBytes();
        bin.close();  //fin会因bin.close而自动关闭
        return bytes;
    }


}
