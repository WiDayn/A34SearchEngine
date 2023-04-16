package cn.edu.hhu.a34searchengine.dao.impl;


import cn.edu.hhu.a34searchengine.dao.PDFDocIndexDao;
import cn.edu.hhu.a34searchengine.dto.SearchCondition;
import cn.edu.hhu.a34searchengine.pojo.PDFDoc;
import cn.edu.hhu.a34searchengine.pojo.PDFDocPage;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.SuggestOption;
import cn.edu.hhu.a34searchengine.vo.SuggestResult;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ScriptLanguage;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.JsonData;
import com.google.errorprone.annotations.DoNotCall;
import org.apache.http.annotation.Obsolete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Repository
public class PDFDocIndexDaoImpl implements PDFDocIndexDao
{
    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    private final HighlightQuery highlightArticleAbstract;
    private final Highlight contentHighlighter;
    private final Highlight imageTextsHighlighter;

    //有两个包定义了Highlight类,不能互转,函数用到了两个包中的Highlight, 写起来麻烦. 故抽取构造高亮查询部分的代码,作为成员常量提前写入
    public PDFDocIndexDaoImpl()
    {
        org.springframework.data.elasticsearch.core.query.highlight.HighlightField highlightField;
        HighlightFieldParameters highlightFieldParameters=new HighlightFieldParameters.HighlightFieldParametersBuilder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .withFragmentSize(200)
                .build();
        highlightField=new org.springframework.data.elasticsearch.core.query.highlight.HighlightField("articleAbstract",highlightFieldParameters);
        org.springframework.data.elasticsearch.core.query.highlight.Highlight highlight=new org.springframework.data.elasticsearch.core.query.highlight.Highlight(List.of(highlightField));
        highlightArticleAbstract =new HighlightQuery(highlight,null);

        HighlightField.Builder pagesImageTextsHFB=new HighlightField.Builder().matchedFields("pages.imageTexts.text");
        HighlightField.Builder pagesContentHFB=new HighlightField.Builder().matchedFields("pages.content");
        Highlight.Builder highlightBuilder=new Highlight.Builder()
                .fragmentSize(300)
                .preTags("<em>")
                .postTags("</em>");
        Highlight.Builder highlightBuilder2=new Highlight.Builder() //builder不能重复使用
                .fragmentSize(300)
                .preTags("<em>")
                .postTags("</em>");
        MatchQuery.Builder matchQB=new MatchQuery.Builder();
        Highlight.Builder highlightPagesContentBuilder=highlightBuilder.fields("pages.content",pagesContentHFB.build());
        Highlight.Builder highlightPagesImageTextsBuilder=highlightBuilder2.fields("pages.imageTexts.text",pagesImageTextsHFB.build());
        imageTextsHighlighter =highlightPagesImageTextsBuilder.build();
        contentHighlighter =highlightPagesContentBuilder.build();
    }

    //关于NativeQueryBuilder的基本公用设置,抽取出来以美化代码
    //设置排除字段,缓存等
    private NativeQueryBuilder _assembleNativeQuery()
    {
        NativeQueryBuilder nativeQB = NativeQuery.builder();
        nativeQB.withSourceFilter(new SourceFilter()
        {
            @Override
            public String[] getIncludes()
            {
                return new String[0];
            }

            @Override
            public String[] getExcludes()
            {
                return new String[]{"pages.content"};   //设置整个文档中的pages.content字段不要返回
            }
        });
       // nativeQB.withRequestCache(true);

        return nativeQB;
    }

    //将SearchCondition翻译为elasticsearch的查询条件
    public BoolQuery.Builder _assembleConditionQuery(SearchCondition condition)
    {
        Timer timer=new Timer();

        BoolQuery.Builder boolQB = new BoolQuery.Builder();
        if (condition.getAuthors() != null && !condition.getAuthors().equals("")) {
            boolQB.must(q -> q.match(f -> f.field("authors").query(condition.getAuthors()).fuzziness("auto")));
        }
        if (condition.getSubsets() != null && !condition.getSubsets().equals("")) {
            boolQB.must(q -> q.match(f -> f.field("subset").query(condition.getSubsets()).fuzziness("auto")));
        }
        if (condition.getGenres() != null && condition.getGenres().length!=0) {
            BoolQuery.Builder subBoolQB = QueryBuilders.bool();
            for (String genre : condition.getGenres()) {
                subBoolQB.should(q -> q.fuzzy(f -> f.field("genre").value(genre)));
            }
            boolQB.must(q -> q.bool(subBoolQB.build()));
        }

        Date pubDateLB = condition.getPubDateLB();
        Date pubDateUB = condition.getPubDateUB();
        if (pubDateLB != null) {
            boolQB.must(q -> q.range(r -> r.field("pubDate").gte(JsonData.of(pubDateLB.getTime()))));
        }
        if (pubDateUB != null) {
            boolQB.must(q -> q.range(r -> r.field("pubDate").lte(JsonData.of(pubDateUB.getTime()))));
        }
        timer.stop();
        return boolQB;
    }

    // PhraseSuggestOption处理为可以序列号的数据结构
    private SuggestResult _assemblePhraseSuggestResult(List<PhraseSuggestOption> suggestOptions){
        SuggestResult suggestResult = new SuggestResult();
        for(PhraseSuggestOption phraseSuggestOption : suggestOptions) {
            SuggestOption suggestOption = new SuggestOption();
            suggestOption.setText(phraseSuggestOption.text());
            suggestOption.setScore(phraseSuggestOption.score());
            suggestResult.setSuggestOption(suggestOption);
        }
        return suggestResult;
    }

    //将几种Query综合build
    private NativeQuery _buildSearch(NativeQueryBuilder nativeQB,BoolQuery.Builder boolQB,ScriptScoreQuery.Builder scriptScoreQB,Pageable pageRequest)
    {
        Timer timer=new Timer();
        scriptScoreQB.query(f->f.bool(boolQB.build()));
        nativeQB.withQuery(q -> q.scriptScore(scriptScoreQB.build())).withPageable(pageRequest);
        timer.stop();
        return nativeQB.build();
    }



    private ScriptScoreQuery.Builder _getScriptScoreQueryBuilder()  //注意与DocHit.Scores.setScores()保持一致
    {
        ScriptScoreQuery.Builder scriptScoreQB=new ScriptScoreQuery.Builder();
        final String originalScoreCastFunc="0.9*saturation(_score, 7)+0.1";
        final String preferenceCastFunc="1/(1+Math.exp(-0.25*(doc['preference'].value-30)))";
        final String script="params.wRelevance*"+originalScoreCastFunc+"+params.wClickRate*doc['clickRate'].value+params.wPreference*" + preferenceCastFunc;
        Map<String,JsonData> params= Map.of("wRelevance",JsonData.of(60),"wClickRate",JsonData.of(30),"wPreference",JsonData.of(10));
        scriptScoreQB.script(f->f.inline(i->i.lang(ScriptLanguage.Painless).params(params).source(script)));
        return scriptScoreQB;
    }


    private NestedQuery.Builder _getPagesContentNQB(){
        return new NestedQuery.Builder().path("pages")  //设置路径为 nested对象 pages
                .innerHits(i -> i.fields("pages").highlight(contentHighlighter).source(s -> s.filter(f -> f.excludes("pages.content","pages.imageTexts"))));
        //设置嵌入文档的命中作为innerHits返回,这样才能得到页号,单页命中等信息        设置嵌套文档pages中的content字段不要返回,节省带宽
    }

    private NestedQuery.Builder _getNestedQueryBuilder(String path, String innerHitsField,Highlight highlighter, String ...excludes){
        return new NestedQuery.Builder().path(path)  //设置路径为 nested对象 pages
                .innerHits(i -> i.fields(innerHitsField).highlight(highlighter).source(s -> s.filter(f -> f.excludes(Arrays.asList(excludes)))));
        //设置嵌入文档的命中作为innerHits返回,这样才能得到页号,单页命中等信息        设置嵌套文档pages中的content字段不要返回,节省带宽
    }


    private void _injectNestedQuery(BoolQuery.Builder boolQB,NestedQuery.Builder nestedQB,String queryKeywords, String field,String fuzziness){
        boolQB.should(q -> q.nested(nestedQB.query(q2 -> q2.match(m -> m.field(field).query(queryKeywords).fuzziness(fuzziness))).build()));
    }

    @Override
    public SearchPage<PDFDoc> searchInContent(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        Timer timer = new Timer();
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB = _assembleConditionQuery(condition);
        //构造NestedQuery查询pdf正文
        NestedQuery.Builder pagesContentNQB = _getNestedQueryBuilder("pages", "pages", contentHighlighter, "pages.content", "pages.imageTexts");
        //构造NestedQuery查询图片中的文字
        NestedQuery.Builder imageTextsNQB = _getNestedQueryBuilder("pages.imageTexts", "pages.imageTexts", imageTextsHighlighter, "pages.imageTexts.text");

        BoolQuery.Builder coreBoolQB = new BoolQuery.Builder();

        //注入查询pdf正文的NestedQuery
        _injectNestedQuery(coreBoolQB, pagesContentNQB, keywords, "pages.content", "auto");

        //注入查询来自图片的文本的NestedQuery
        _injectNestedQuery(coreBoolQB, imageTextsNQB, keywords, "pages.imageTexts.text", "auto");

        boolQB.must(b -> b.bool(coreBoolQB.build()));

        //获取打分规则
        ScriptScoreQuery.Builder scriptScoreQB = _getScriptScoreQueryBuilder();

        //构造查询
        NativeQuery query = _buildSearch(nativeQB, boolQB, scriptScoreQB, pageRequest);

        SearchHits<PDFDoc> searchHits = elasticsearchOperations.search(query, PDFDoc.class);
        SearchPage<PDFDoc> searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        timer.stop();
        return searchPage;
    }
    @Override
    public SearchPage<PDFDoc> searchInImageText(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        Timer timer=new Timer();
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB=_assembleConditionQuery(condition);

        //构造NestedQuery查询图片中的文字
        NestedQuery.Builder imageTextsNQB = _getNestedQueryBuilder("pages.imageTexts","pages.imageTexts", imageTextsHighlighter,"pages.imageTexts.text");

        //注入查询来自图片的文本的NestedQuery
        _injectNestedQuery(boolQB,imageTextsNQB,keywords,"pages.imageTexts.text","auto");

        //获取打分规则
        ScriptScoreQuery.Builder scriptScoreQB=_getScriptScoreQueryBuilder();

        //构造查询
        NativeQuery query = _buildSearch(nativeQB,boolQB,scriptScoreQB,pageRequest);

        SearchHits<PDFDoc> searchHits = elasticsearchOperations.search(query, PDFDoc.class);
        SearchPage<PDFDoc> searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        timer.stop();
        return searchPage;
    }

    @Override
    public SearchPage<PDFDoc> searchInAbstract(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        Timer timer=new Timer();
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB=_assembleConditionQuery(condition);

        //设置查询字段为摘要
        boolQB.must(q -> q.match(m -> m.field("articleAbstract").query(keywords).fuzziness("auto")));
        //设置高亮
        nativeQB.withHighlightQuery(highlightArticleAbstract);

        //获取打分规则
        ScriptScoreQuery.Builder scriptScoreQB=_getScriptScoreQueryBuilder();

        //构造查询
        NativeQuery query = _buildSearch(nativeQB,boolQB,scriptScoreQB,pageRequest);

        SearchHits<PDFDoc> searchHits = elasticsearchOperations.search(query, PDFDoc.class);
        SearchPage<PDFDoc> searchPage = SearchHitSupport.searchPageFor(searchHits, pageRequest);
        timer.stop();
        return searchPage;
    }

    public SuggestResult searchPhraseSuggest(String keywords) throws IOException {
        SearchResponse<PDFDoc> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index("pdf_doc_2")
                        .suggest(suggesterBuilder -> suggesterBuilder
                                .suggesters("success_suggest", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .text(keywords)
                                        .phrase(phraseSuggestBuilder -> phraseSuggestBuilder
                                                .field("pages.content")
                                        )
                                )
                        )
                , PDFDoc.class);

        return _assemblePhraseSuggestResult(response.suggest().get("success_suggest").get(0).phrase().options());
    }


}
