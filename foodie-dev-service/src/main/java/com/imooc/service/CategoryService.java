package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;

import java.util.List;

/**
 * 轮播图
 *
 * @author Wenqi Liang
 * @date 2020/12/19
 */
public interface CategoryService {
    /**
     * 查询所有一级分类
     *
     * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id, 查询子分类信息
     *
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);
}
