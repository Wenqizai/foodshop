package com.imooc.pojo.vo;

import lombok.Data;

/**
 * 用于展示商品评价数量的VO
 *
 * @author liangwq
 * @date 2020/12/20
 */
@Data
public class CommentLevelCountsVO {

    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;

}
