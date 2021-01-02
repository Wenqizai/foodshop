package com.imooc.pojo.vo;

import lombok.Data;

/**
 * 订单VO, 用于返回个controller
 */
@Data
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;

}