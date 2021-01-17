package com.imooc.pojo.bo.center;

import lombok.Data;
import lombok.ToString;

/**
 * @author liangwq
 * @date 2021/1/16
 */
@Data
@ToString
public class OrderItemsCommentBO {

    private String commentId;
    private String itemId;
    private String itemName;
    private String itemSpecId;
    private String itemSpecName;
    private Integer commentLevel;
    private String content;

}
