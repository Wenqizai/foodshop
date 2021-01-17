package com.imooc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 订单状态概览数量VO
 * @author liangwq
 * @date 2021/1/17
 */
@Data
@AllArgsConstructor
public class OrderStatusCountsVO {

    private Integer waitPayCounts;
    private Integer waitDeliverCounts;
    private Integer waitReceiveCounts;
    private Integer waitCommentCounts;

}
