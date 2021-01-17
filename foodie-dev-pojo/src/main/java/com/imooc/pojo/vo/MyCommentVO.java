package com.imooc.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liangwq
 * @date 2021/1/17
 */
@Data
public class MyCommentVO {

    private String commentId;
    private String content;
    private Date createdTime;
    private String itemId;
    private String itemName;
    private String specName;
    private String itemImg;

}
