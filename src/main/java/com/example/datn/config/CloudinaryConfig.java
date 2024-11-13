package com.example.datn.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhqhqnxvh",
                "api_key", "224186211154314",
                "api_secret", "qwdQJdGWEWEHn9yj2fA-qAKLFZU"));
    }
}