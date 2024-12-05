package com.example.datn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Địa chỉ frontend của bạn
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Các phương thức HTTP cần thiết
                .allowedHeaders("*") // Cho phép tất cả headers
                .allowCredentials(true); // Nếu bạn cần gửi cookie hoặc token
    }
}
