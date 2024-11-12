package com.enigwed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images from /app/images folder as static resources accessible via /images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/home/enigma/asset/images/"); // Change path if using another directory
    }
}
