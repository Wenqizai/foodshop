package com.imooc.pojo.vo;

import com.imooc.pojo.bo.ShopcartBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单VO, 用于返回个controller
 */
@Data
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    /**
     * 用于保存下订单后保存需要删除的商品信息
     */
    private List<ShopcartBO> toBeRemovedShopcartList;

}