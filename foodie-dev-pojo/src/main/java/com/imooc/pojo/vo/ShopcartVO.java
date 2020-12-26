package com.imooc.pojo.vo;

import lombok.Data;

/**
 * 购物车BO
 * @author liangwq
 * @date 2020/12/24
 */
@Data
public class ShopcartVO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private String priceDiscount;
    private String priceNormal;

}
