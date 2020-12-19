package com.imooc.service.impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.pojo.Carousel;
import com.imooc.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 轮播图Service
 * @author liangwq
 * @date 2020/12/19
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    CarouselMapper carouselMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> queryAll(Integer isShow) {
        Example example = new Example(Carousel.class);
        example.createCriteria().andEqualTo("isShow", isShow);
        example.orderBy("sort").desc();
        List<Carousel> carouselList = carouselMapper.selectByExample(example);
        return carouselList;
    }
}
