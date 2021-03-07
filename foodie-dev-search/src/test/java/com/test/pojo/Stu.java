package com.test.pojo;

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
@Document(indexName = "stu", type = "_doc")
@Data
@ToString
public class Stu {

    @Id
    private Long stuId;
    @Field(store = true, index = false)
    private String name;
    @Field(store = true)
    private Integer age;
    @Field(store = true, type = FieldType.Keyword)
    private String sign;
    @Field(store = true)
    private String description;
    @Field(store = true)
    private float money;

}
