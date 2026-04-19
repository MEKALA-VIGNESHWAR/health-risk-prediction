package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig - Spring MVC routing configuration
 * Maps frontend routes to their HTML pages for SPA support
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Auth routes (SPA support)
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/login.html");
        
        // Dashboard routes
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/patient-dashboard").setViewName("forward:/patient-dashboard.html");
        registry.addViewController("/doctor-dashboard").setViewName("forward:/doctor-dashboard.html");
        registry.addViewController("/enhanced-dashboard").setViewName("forward:/enhanced-dashboard.html");
    }
}
