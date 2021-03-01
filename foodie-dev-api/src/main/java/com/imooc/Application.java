package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author liangwq
 * @date 2020/11/29
 */
//@EnableTransactionManagement
@EnableScheduling
@ComponentScan({"com.imooc", "org.n3r.idworker"})
@MapperScan(basePackages = {"com.imooc.mapper"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableRedisHttpSession // 开启使用Redis作为Spring session
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
