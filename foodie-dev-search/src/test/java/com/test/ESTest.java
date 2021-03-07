package com.test;

import com.imooc.SearchApplication;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangwq
 * @date 2021/3/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class ESTest {

    @Autowired
    ElasticsearchTemplate esTemplate;

    @Test
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setStuId(1003L);
        stu.setName("spider1 man");
        stu.setAge(23);
        stu.setMoney(18.8f);
        stu.setSign("i am spider3 man");
        stu.setDescription("i wish i am spider3 man");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    /**
     * 不建议使用ElasticsearchTemplate对索引进行管理(创建索引, 更新映射, 删除索引)
     * 索引就像是数据库或数据库的表
     * 我们只针对数据做CRUD操作
     */
    @Test
    public void deleteIndex() {
        esTemplate.deleteIndex(Stu.class);
    }


    //####################################################

    @Test
    public void updateStuDoc() {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign", "i am not super man");
        sourceMap.put("money", 88.6f);
        sourceMap.put("age", 33);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);
        UpdateQuery query = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1002")
                .withIndexRequest(indexRequest)
                .build();
        esTemplate.update(query);
    }

    @Test
    public void getStu() {
        GetQuery query = new GetQuery();
        query.setId("1002");
        Stu stu = esTemplate.queryForObject(query, Stu.class);
        System.out.println(stu.toString());
    }

    @Test
    public void deleteStuDoc() {
        esTemplate.delete(Stu.class, "1002");
    }

    //####################################################

    @Test
    public void searchStuDoc() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "save man"))
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> pageStu = esTemplate.queryForPage(query, Stu.class);
        System.out.println("检索后的总分页数目为:" + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        for (Stu stu : stuList) {
            System.out.println(stu);
        }
    }

    @Test
    public void highlightStuDoc() {
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(0, 10);
        SortBuilder moneySortBuilder = new FieldSortBuilder("money").order(SortOrder.DESC);
        SortBuilder ageSortBuilder = new FieldSortBuilder("age").order(SortOrder.DESC);
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "save man"))
                .withHighlightFields(new HighlightBuilder
                        .Field("description")
                        .preTags(preTag)
                        .postTags(postTag))
                        .withSort(moneySortBuilder)
                        .withSort(ageSortBuilder)
                .withPageable(pageable)
                .build();
        AggregatedPage<Stu> pageStu = esTemplate.queryForPage(query, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Stu> stuListHighlight = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();
                    Object stuId = hit.getSourceAsMap().get("stuId");
                    String name = (String) hit.getSourceAsMap().get("name");
                    String sign = (String) hit.getSourceAsMap().get("sign");
                    Integer age = (Integer) hit.getSourceAsMap().get("age");
                    Object money = hit.getSourceAsMap().get("money");
                    Stu stuHL = new Stu();
                    stuHL.setStuId(Long.valueOf(stuId.toString()));
                    stuHL.setName(name);
                    stuHL.setAge(age);
                    stuHL.setSign(sign);
                    stuHL.setMoney(Float.valueOf(money.toString()));
                    stuHL.setDescription(description);
                    stuListHighlight.add(stuHL);
                }
                if (stuListHighlight.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) stuListHighlight);
                }
                return null;
            }
        });
        System.out.println("检索后的总分页数目为:" + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        for (Stu stu : stuList) {
            System.out.println(stu);
        }
    }

}
