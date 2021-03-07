package com.imooc.es.pojo;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

/**
 * @author liangwq
 * @date 2021/3/6
 */
@Document(indexName = "foodie-items-ik", type = "doc", createIndex = false)
@Data
@ToString
public class Items {

    @Id
    @Field(store = true, type = FieldType.Text, index = false)
    private String itemId;
    @Field(store = true, type = FieldType.Text, index = true)
    private String itemName;
    @Field(store = true, type = FieldType.Text, index = false)
    private String imgUrl;
    @Field(store = true, type = FieldType.Integer)
    private Integer price;
    @Field(store = true, type = FieldType.Integer)
    private Integer sellCounts;

}
