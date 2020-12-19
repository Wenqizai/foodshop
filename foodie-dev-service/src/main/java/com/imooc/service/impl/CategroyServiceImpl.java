package com.imooc.service.impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.mapper.CategoryMapper;
import com.imooc.mapper.CategoryMapperCustom;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 商品分类Service
 * @author liangwq
 * @date 2020/12/19
 */
@Service
public class CategroyServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("type", 1);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        List<CategoryVO> subCatList = categoryMapperCustom.getSubCatList(rootCatId);
        return subCatList;
    }

}
