package com.example.tachesapp.Config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

public class ConfigResources {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
