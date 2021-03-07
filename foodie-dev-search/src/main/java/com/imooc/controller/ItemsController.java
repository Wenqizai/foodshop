package com.imooc.controller;

import com.imooc.service.ItemESService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liangwq
 * @date 2021/3/7
 */
@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    ItemESService itemESService;

    @GetMapping("/hello")
    public Object hello() {
        return "hello controller!";
    }

    @GetMapping("/es/search")
    public IMOOCJSONResult search(
                            String keywords,
                            String sort,
                            Integer page,
                            Integer pageSize) {
        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        page --;
        PagedGridResult grid = itemESService.searchItems(keywords, sort, page, pageSize);
        return IMOOCJSONResult.ok(grid);
    }

}
