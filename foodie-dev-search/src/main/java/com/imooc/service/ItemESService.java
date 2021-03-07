package com.imooc.service;

import com.imooc.utils.PagedGridResult;

/**
 * @author liangwq
 * @date 2021/3/7
 */
public interface ItemESService {

    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

}
