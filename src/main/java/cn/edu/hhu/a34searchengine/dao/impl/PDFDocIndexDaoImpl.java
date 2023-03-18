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
    private final Highlight highlightPagesContent;
    private final Highlight highlightPagesImageTexts;

    //有两个包定义了Highlight类,不能互转,函数用到了两个包中的Highlight, 写起来麻烦. 故抽取构造高亮查询部分的代码,作为成员常量提前写入
    public PDFDocIndexDaoImpl()
    {
        org.springframework.data.elasticsearch.core.query.highlight.HighlightField highlightField;
        HighlightFieldParameters highlightFieldParameters=new HighlightFieldParameters.HighlightFieldParametersBuilder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .withFragmentSize(100)
                .build();
        highlightField=new org.springframework.data.elasticsearch.core.query.highlight.HighlightField("articleAbstract",highlightFieldParameters);
        org.springframework.data.elasticsearch.core.query.highlight.Highlight highlight=new org.springframework.data.elasticsearch.core.query.highlight.Highlight(List.of(highlightField));
        highlightArticleAbstract =new HighlightQuery(highlight,null);

        HighlightField.Builder pagesImageTextsHFB=new HighlightField.Builder().matchedFields("pages.imageTexts");
        HighlightField.Builder pagesContentHFB=new HighlightField.Builder().matchedFields("pages.content");
        Highlight.Builder highlightBuilder=new Highlight.Builder()
                .fragmentSize(100)
                .preTags("<em>")
                .postTags("</em>");
        Highlight.Builder highlightBuilder2=new Highlight.Builder() //builder不能重复使用
                .fragmentSize(100)
                .preTags("<em>")
                .postTags("</em>");
        MatchQuery.Builder matchQB=new MatchQuery.Builder();
        Highlight.Builder highlightPagesContentBuilder=highlightBuilder.fields("pages.content",pagesContentHFB.build());
        Highlight.Builder highlightPagesImageTextsBuilder=highlightBuilder2.fields("pages.imageTexts",pagesImageTextsHFB.build());
        highlightPagesImageTexts =highlightPagesImageTextsBuilder.build();
        highlightPagesContent=highlightPagesContentBuilder.build();
    }

    //关于NativeQueryBuilder的基本公用设置,抽取出来以美化代码
    //设置排除字段,分页,排名规则,得分规则,缓存等
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
        FunctionScoreQuery.Builder functionScoreQuery=new FunctionScoreQuery.Builder();

        BoolQuery.Builder boolQB = new BoolQuery.Builder();
        if (condition.getAuthors() != null) {
            BoolQuery.Builder subBoolQB = QueryBuilders.bool();
            for (String author : condition.getAuthors()) {
                subBoolQB.should(q -> q.term(t -> t.field("authors").value(author)));
            }
            boolQB.must(q -> q.bool(subBoolQB.build()));
        }
        if (condition.getSubsets() != null) {
            BoolQuery.Builder subBoolQB = QueryBuilders.bool();
            for (String subset : condition.getSubsets()) {
                subBoolQB.should(q -> q.term(t -> t.field("subset").value(subset)));
            }
            boolQB.must(q -> q.bool(subBoolQB.build()));
        }
        if (condition.getGenres() != null) {
            BoolQuery.Builder subBoolQB = QueryBuilders.bool();
            for (String genre : condition.getGenres()) {
                subBoolQB.should(q -> q.term(t -> t.field("genre").value(genre)));
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

    private SearchPage<PDFDoc> _buildSearch(NativeQueryBuilder nativeQB,BoolQuery.Builder boolQB,Pageable pageRequest)
    {
        Timer timer=new Timer();
        nativeQB.withQuery(q -> q.bool(boolQB.build())).withPageable(pageRequest);
        SearchHits<PDFDoc> searchHits = elasticsearchOperations.search(nativeQB.build(), PDFDoc.class);
        SearchPage<PDFDoc> searchPage = SearchHitSupport.searchPageFor(searchHits, nativeQB.getPageable());
        timer.stop();
        return searchPage;
    }

    private SearchPage<PDFDoc> _buildSearch(NativeQueryBuilder nativeQB,ScriptScoreQuery.Builder scriptScoreQB,Pageable pageRequest)
    {
        Timer timer=new Timer();
        nativeQB.withQuery(q -> q.scriptScore(scriptScoreQB.build())).withPageable(pageRequest);
        SearchHits<PDFDoc> searchHits = elasticsearchOperations.search(nativeQB.build(), PDFDoc.class);
        SearchPage<PDFDoc> searchPage = SearchHitSupport.searchPageFor(searchHits, nativeQB.getPageable());
        timer.stop();
        return searchPage;
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

    @Override
    public SearchPage<PDFDoc> searchInContent(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        Timer timer=new Timer();
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB=_assembleConditionQuery(condition);
        //构造nested嵌入文档查询
        NestedQuery.Builder pagesContentNQB = new NestedQuery.Builder().path("pages")  //设置路径为 nested对象 pages
                .innerHits(i -> i.fields("pages").highlight(highlightPagesContent).source(s -> s.filter(f -> f.excludes("pages.content"))));
                //设置嵌入文档的命中作为innerHits返回,这样才能得到页号,单页命中等信息        设置嵌套文档pages中的content字段不要返回,节省带宽

        boolQB.must(q -> q.nested(pagesContentNQB.query(q3 -> q3.match(m -> m.field("pages.content")
                        .query(keywords).fuzziness("auto")))
                        .build()));
        ScriptScoreQuery.Builder scriptScoreQB=_getScriptScoreQueryBuilder();
        scriptScoreQB.query(f->f.bool(boolQB.build()));
        //构造查询,发送到elasticsearch并返回结果
        SearchPage<PDFDoc> searchPage =_buildSearch(nativeQB,scriptScoreQB,pageRequest);
        timer.stop();
        return searchPage;
    }
    @Override
    public SearchPage<PDFDoc> searchInImageText(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB=_assembleConditionQuery(condition);
        NestedQuery.Builder pagesContentNQB = new NestedQuery.Builder().path("pages")  //设置路径为 nested对象 pages
                .innerHits(i -> i.name("pages").highlight(highlightPagesImageTexts).source(s -> s.filter(f -> f.excludes("pages.imageTexts"))));
        //设置嵌入文档的命中作为innerHits返回,这样才能得到页号,单页命中等信息        设置嵌套文档pages中的content字段不要返回,节省带宽
        boolQB.must(q -> q.nested(pagesContentNQB.query(q2 -> q2.match(m -> m.field("pages.imageTexts").query(keywords).fuzziness("auto"))).build()));
        //构造查询,发送到elasticsearch并返回结果
        return _buildSearch(nativeQB,boolQB,pageRequest);
    }

    @Override
    public SearchPage<PDFDoc> searchInAbstract(String keywords, SearchCondition condition, Pageable pageRequest)
    {
        //注入查询配置
        NativeQueryBuilder nativeQB = _assembleNativeQuery();
        //注入查询条件
        BoolQuery.Builder boolQB=_assembleConditionQuery(condition);
        boolQB.must(q -> q.match(m -> m.field("articleAbstract").query(keywords).fuzziness("auto")));
        nativeQB.withHighlightQuery(highlightArticleAbstract);
        //构造查询,发送到elasticsearch并返回结果
        return _buildSearch(nativeQB,boolQB,pageRequest);
    }


    @Deprecated
    @Obsolete
    @DoNotCall
    private SearchHits<PDFDoc> findByKeywords2(String keywords, SearchCondition condition)
    {
        //构造查询条件
        Criteria criteria = new Criteria();
        if (condition.getAuthors() != null) {
            Criteria subCriteria = new Criteria();
            for (String author : condition.getAuthors()) {
                subCriteria = subCriteria.or("authors").is(author);
            }
            criteria.and(subCriteria);
        }
        if (condition.getSubsets() != null) {
            Criteria subCriteria = new Criteria();
            for (String subset : condition.getSubsets()) {
                subCriteria = subCriteria.or("subset").is(subset);
            }
            criteria.and(subCriteria);
        }
        if (condition.getGenres() != null) {
            Criteria subCriteria = new Criteria();
            for (String genre : condition.getGenres()) {
                subCriteria = subCriteria.or("genre").is(genre);
            }
            criteria.and(subCriteria);
        }
        if (condition.getPubDateLB() != null) {
            criteria = criteria.and("pubDate").greaterThanEqual(condition.getPubDateLB().getTime());
        }
        if (condition.getPubDateUB() != null) {
            criteria = criteria.and("pubDate").lessThanEqual(condition.getPubDateUB().getTime());
        }

        org.springframework.data.elasticsearch.core.query.highlight.HighlightField highlightField;
        HighlightFieldParameters highlightFieldParameters=new HighlightFieldParameters.HighlightFieldParametersBuilder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .withFragmentSize(100)
                .build();
        if (condition.isSearchInAbstractsOnly()) {
            criteria = criteria.and("articleAbstract");
            highlightField=new org.springframework.data.elasticsearch.core.query.highlight.HighlightField("articleAbstract",highlightFieldParameters);
        } else if (condition.isSearchInTextsFromImagesOnly()) {
            criteria = criteria.and("imageTexts");
            highlightField=new org.springframework.data.elasticsearch.core.query.highlight.HighlightField("imageTexts",highlightFieldParameters);
        } else {
            criteria = criteria.and("content");
            highlightField=new org.springframework.data.elasticsearch.core.query.highlight.HighlightField("content",highlightFieldParameters);
        }
        criteria = criteria.matches(keywords);
        org.springframework.data.elasticsearch.core.query.highlight.Highlight highlight=new org.springframework.data.elasticsearch.core.query.highlight.Highlight(List.of(highlightField));
        HighlightQuery highlightQuery=new HighlightQuery(highlight, PDFDocPage.class);
        org.springframework.data.elasticsearch.core.query.Query query = new CriteriaQuery(criteria);
        query.setHighlightQuery(highlightQuery);
        return elasticsearchOperations.search(query, PDFDoc.class);
    }

    public SuggestResult searchPhraseSuggest(String keywords) throws IOException {
        SearchResponse<PDFDoc> response = elasticsearchClient.search(searchRequestBuilder -> searchRequestBuilder
                        .index("")
                        .suggest(suggesterBuilder -> suggesterBuilder
                                .suggesters("success_suggest", fieldSuggesterBuilder -> fieldSuggesterBuilder
                                        .text(keywords)
                                        .phrase(phraseSuggestBuilder -> phraseSuggestBuilder
                                                .field("content")
                                        )
                                )
                        )
                , PDFDoc.class);

        return _assemblePhraseSuggestResult(response.suggest().get("success_suggest").get(0).phrase().options());
    }
}
