package com.lantu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration

public class MyCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        //添加cors配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        //允许的域 不要写* 否则cookie没法使用了
        configuration.addAllowedOrigin("http://localhost:8888");
        //是否发送cookie信息
        configuration.setAllowCredentials(true);
        //允许的请求方式
        configuration.addAllowedMethod("*");
        //允许的头信息
        configuration.addAllowedHeader("*");

        //添加映射路径 拦截一切请求
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
