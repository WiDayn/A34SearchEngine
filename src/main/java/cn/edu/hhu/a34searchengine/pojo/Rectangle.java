package cn.edu.hhu.a34searchengine.pojo;


import lombok.Data;

@Data
public class Rectangle
{
    protected float x; //在pdf中的坐标,从左下角开始. 单位:user space unit
    protected float y;
    protected float height; //图片的高和宽. 单位:user space unit
    protected float width;

    public Rectangle(float x,float y,float width,float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "pos:("+x+","+y+")\n"+"width:"+width+"\nheight:"+height;
    }
}
