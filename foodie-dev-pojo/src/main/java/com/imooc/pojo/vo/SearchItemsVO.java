package com.imooc.pojo.vo;

import lombok.Data;

/**
 * 用于展示商品搜索列表结果的VO
 *
 * @author liangwq
 * @date 2020/12/20
 */
@Data
public class SearchItemsVO {

    private String itemId;
    private String itemName;
    private int sellCounts;
    private String imgUrl;
    private int price;

}
