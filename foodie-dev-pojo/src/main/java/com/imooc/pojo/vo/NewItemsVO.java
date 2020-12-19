package com.imooc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 6个最新商品的简单数据类型
 *
 * @author liangwq
 * @date 2020/12/19
 */
@Data
public class NewItemsVO {

    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;

    private List<SimpleItemVO> simpleItemList;
}
