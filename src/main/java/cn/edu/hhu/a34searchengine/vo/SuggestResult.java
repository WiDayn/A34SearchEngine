package cn.edu.hhu.a34searchengine.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonView(SearchResult.SearchResultView.class)
public class SuggestResult {
    List<SuggestOption> suggestOptions;

    public SuggestResult(){
        suggestOptions = new ArrayList<>();
    }

    public void setSuggestOption(SuggestOption suggestOption)
    {
        suggestOptions.add(suggestOption);
    }
}
