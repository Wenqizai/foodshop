package com.imooc.service.impl;

import com.imooc.es.pojo.Items;
import com.imooc.service.ItemESService;
import com.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwq
 * @date 2021/3/7
 */
@Service
public class ItemServiceImpl implements ItemESService {

    @Autowired
    ElasticsearchTemplate esTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
//        String preTag = "<font color='red'>";
//        String postTag = "</font>";
        Pageable pageable = PageRequest.of(page, pageSize);
//        SortBuilder moneySortBuilder = new FieldSortBuilder("money").order(SortOrder.DESC);
//        SortBuilder ageSortBuilder = new FieldSortBuilder("age").order(SortOrder.DESC);
        String itemNameFiled = "itemName";
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameFiled, keywords))
                .withHighlightFields(new HighlightBuilder
                        .Field(itemNameFiled)
//                        .preTags(preTag)
//                        .postTags(postTag)
                        )
//                .withSort(moneySortBuilder)
//                .withSort(ageSortBuilder)
                .withPageable(pageable)
                .build();
        AggregatedPage<Items> pageItem = esTemplate.queryForPage(query, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Items> itemsListHighlight = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get(itemNameFiled);
                    String itemName = highlightField.getFragments()[0].toString();
                    String itemId = (String) hit.getSourceAsMap().get("itemId");
                    String imgUrl = (String) hit.getSourceAsMap().get("imgUrl");
                    Integer price = (Integer) hit.getSourceAsMap().get("price");
                    Integer sellCounts = (Integer) hit.getSourceAsMap().get("sellCounts");
                    Items item = new Items();
                    item.setItemId(itemId);
                    item.setItemName(itemName);
                    item.setImgUrl(imgUrl);
                    item.setPrice(price);
                    item.setSellCounts(sellCounts);
                    itemsListHighlight.add(item);
                }
                return new AggregatedPageImpl<>((List<T>) itemsListHighlight, pageable, searchResponse.getHits().totalHits);
            }
        });
        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(pageItem.getContent());
        pagedGridResult.setPage(page + 1);
        pagedGridResult.setTotal(pageItem.getTotalPages());
        pagedGridResult.setRecords(pageItem.getTotalElements());
        return pagedGridResult;
    }
}
