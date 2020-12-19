package com.imooc.service;

import com.imooc.pojo.Carousel;

import java.util.List;

/**
 * 轮播图
 *
 * @author Wenqi Liang
 * @date 2020/12/19
 */
public interface CarouselService {
    /**
     * 查询所有的轮播图
     * @param isShow
     * @return
     */
    public List<Carousel> queryAll(Integer isShow);
}
