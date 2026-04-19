package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * JPA Configuration for Supabase PostgreSQL
 * Spring Boot auto-configures JPA based on application.properties.
 * This class exists as extension point for future custom JPA settings.
 */
@Configuration
public class JpaConfig {
    // JPA is auto-configured via application.properties:
    //   - PostgreSQL dialect
    //   - HikariCP connection pool
    //   - DDL auto-update
}
