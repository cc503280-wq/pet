package com.pet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 當網址請求 /memberImages/** 時，對應到實體路徑 C:/memberImages/
        registry.addResourceHandler("/memberImages/**")
                .addResourceLocations("file:///C:/memberImages/");
	}

	
}
