package cn.edu.hhu.a34searchengine.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
public class SearchCondition
{
    @DateTimeFormat(pattern = "yyyy",fallbackPatterns = "yyyy-MM-dd")
    Date pubDateUB;    //发表日期上界

    @DateTimeFormat(pattern = "yyyy",fallbackPatterns = "yyyy-MM-dd")
    Date pubDateLB;   //发表日期下界

    String[] authors;  //作者,在含有xx作者的文章中查询, 前端有传递数组的方法

    String[] subsets;  //期刊名或保存机构名,在某些期刊/保存机构内查询

    String[] genres;     //论文类型,在xx类型论文中查询

    boolean searchInAbstractsOnly;   //只在摘要中查询开关, 打开后只查询"paperAbstract"域,而不会查询"content"域
    // 为false则摘要跟正文都被搜索,因为摘要与正文在提取pdf文本时分不开,均存储在"content"域,
    // 即使为true也不能保证搜索到所有摘要,因为摘要信息可能在上传时未填写

    boolean searchInTextsFromImagesOnly;  //只在来自图片的文本中查询开关
    //为false则图片文本跟正文都被搜索,true则只搜索图片中的文本
    //为简化逻辑, searchInAbstractsOnly 与 searchInTextsFromImagesOnly 不能同为true

}
