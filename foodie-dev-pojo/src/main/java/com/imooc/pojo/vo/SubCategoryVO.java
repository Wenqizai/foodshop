package com.imooc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author liangwq
 * @date 2020/12/19
 */
@Data
public class SubCategoryVO {

    private Integer subId;
    private String subName;
    private String subType;
    private Integer subFatherId;

}
