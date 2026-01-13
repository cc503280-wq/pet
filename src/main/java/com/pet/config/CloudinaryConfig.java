package com.pet.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        // 填入 Cloudinary Dashboard 上的資料
        config.put("cloud_name", "dwzbhnqmq");
        config.put("api_key", "862272849312354");
        config.put("api_secret", "HN5Kc92XuyGUHsCUTKMhrp7oF0g");
        
        return new Cloudinary(config);
    }
}
