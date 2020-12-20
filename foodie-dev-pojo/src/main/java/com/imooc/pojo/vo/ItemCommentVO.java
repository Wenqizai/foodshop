package com.imooc.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用于展示商品评价的VO
 *
 * @author liangwq
 * @date 2020/12/20
 */
@Data
public class ItemCommentVO {

    private Integer commentLevel;
    private String content;
    private String specName;
    private Date createdName;
    private  String userFace;
    private String nickname;

}
