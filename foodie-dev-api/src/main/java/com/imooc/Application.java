package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author liangwq
 * @date 2020/11/29
 */
//@EnableTransactionManagement
@ComponentScan({"com.imooc", "org.n3r.idworker"})
@MapperScan(basePackages = {"com.imooc.mapper"})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
