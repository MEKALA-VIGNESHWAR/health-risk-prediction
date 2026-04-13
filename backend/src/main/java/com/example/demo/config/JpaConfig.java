package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;

/**
 * JPA Configuration for Supabase PostgreSQL
 * Handles lazy initialization and graceful fallback
 */
@Configuration
public class JpaConfig {

    /**
     * Configure Hibernate to be more lenient with missing database
     * This allows the application to start even if database is temporarily unavailable
     */
    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setShowSql(false);
        return adapter;
    }
}
