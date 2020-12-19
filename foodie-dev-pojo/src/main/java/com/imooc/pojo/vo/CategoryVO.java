package com.imooc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 二级分类VO
 *
 * @author liangwq
 * @date 2020/12/19
 */
@Data
public class CategoryVO {

    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;
    /**
     * 三级分类VO List
     */
    private List<SubCategoryVO> subCatList;
}
