package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig - Configuration for Spring MVC
 * Handles routing for single-page application (SPA)
 * Forwards all unknown routes to index.html for client-side routing
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward root path to index.html (login page)
        registry.addViewController("/").setViewName("forward:/index.html");
        
        // Forward login-related paths to index.html (SPA routing)
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/register").setViewName("forward:/index.html");
        
        // Forward dashboard paths to index.html (SPA routing)
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/patient-dashboard").setViewName("forward:/index.html");
        registry.addViewController("/doctor-dashboard").setViewName("forward:/index.html");
        
        // Forward enhanced dashboard path
        registry.addViewController("/enhanced-dashboard").setViewName("forward:/enhanced-dashboard.html");
    }
}
