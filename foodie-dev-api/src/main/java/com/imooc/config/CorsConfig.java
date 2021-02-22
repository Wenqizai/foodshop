package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 处理跨域
 *
 * @author liangwq
 * @date 2020/12/13
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 设置url来源
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedOrigin("http://192.168.8.116:8080");
        corsConfiguration.addAllowedOrigin("http://192.168.8.116");
        corsConfiguration.addAllowedOrigin("*");
        // 设置是否发送cookie信息
        corsConfiguration.setAllowCredentials(true);
        // 允许的请求方式
        corsConfiguration.addAllowedMethod("*");
        // 允许的Header
        corsConfiguration.addAllowedHeader("*");

        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsConfigurationSource);
    }
}
