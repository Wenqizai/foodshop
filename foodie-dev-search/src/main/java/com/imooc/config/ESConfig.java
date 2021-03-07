package com.imooc.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author liangwq
 * @date 2021/3/6
 */
@Configuration
public class ESConfig {

    /***解决netty引起的issue*/
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}
