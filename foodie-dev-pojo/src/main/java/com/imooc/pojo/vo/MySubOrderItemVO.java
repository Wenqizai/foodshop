package com.imooc.pojo.vo;

/**
 * @author liangwq
 * @date 2021/1/10
 */

import lombok.Data;

/**
 * 用户中心，我的订单列表嵌套商品VO
 */
@Data
public class MySubOrderItemVO {

    private String itemId;
    private String itemImg;
    private String itemName;
    private String itemSpecName;
    private Integer buyCounts;
    private Integer price;

}
