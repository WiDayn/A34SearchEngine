package cn.edu.hhu.a34searchengine.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonView(SearchResult.SearchResultView.class)
public class SuggestOption {
    String text;

    Double score;
}
