package cn.edu.hhu.a34searchengine.vo;

import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonView(SearchResult.SearchResultView.class)
public class DocHit
{
    PDFDoc docInfo;
    private int highlightCount;    //整个文档的总高亮数

    private Scores scores;

    ContentHit[] contentHits;          //文档内部每页的命中情况

    ImageTextHit[] imageTextHits;

    AbstractHit abstractHit;

    @Data
    @JsonView(SearchResult.SearchResultView.class)
    public static class AbstractHit
    {
        int highlightCount;      //这一页的总高亮数,也是highlights数组的元素个数

        String[] highlights;
    }

    @Data
    @JsonView(SearchResult.SearchResultView.class)
    public static class ContentHit
    {
        int pageNumber;

        int highlightCount;      //这一页的总高亮数,也是highlights数组的元素个数

        String[] highlights;
    }

    @Data
    @JsonView(SearchResult.SearchResultView.class)
    public static class ImageTextHit
    {
        int pageNumber;

        Elem[] highlightElems;

        @Data
        @JsonView(SearchResult.SearchResultView.class)
        public static class Elem{
            String[] highlights;

            float[] baseCoordinate;
            float[][] points;
        }

    }

    @Data
    @JsonView(SearchResult.SearchResultView.class)
    public static class Scores
    {
        float relevance;

        float clickRate;

        float preference;
    }


    public void setScores(float totalScore){      //根据打分规则反推各项得分,注意与script_score保持一致
        scores=new Scores();
        float clickRateScore=docInfo.getClickRate()*30;
        float preferenceScore=(float) (1/(1+Math.exp(-0.25*(docInfo.getPreference()-30))))*10;
        scores.setRelevance(totalScore-clickRateScore-preferenceScore);
        scores.setPreference(preferenceScore);
        scores.setClickRate(clickRateScore);
    }

}
