package com.imooc.controller;

import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liangwq
 * @date 2020/11/29
 */
@RestController
public class HelloController {

    @Autowired
    private StuService stuService;

    @GetMapping("/hello")
    public Object hello(int id) {
        return stuService.getStuInfo(id);
    }


}
