package com.imooc.pojo.bo;

import lombok.Data;

/**
 * 购物车BO
 * @author liangwq
 * @date 2020/12/24
 */
@Data
public class ShopcartBO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private String priceDiscount;
    private String priceNormal;

}
