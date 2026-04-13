package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Bean;

/**
 * Supabase Configuration - Sets up Supabase credentials and REST API client
 */
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url:https://placeholder.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.anon-key:placeholder}")
    private String anonKey;

    @Value("${supabase.service-role-key:placeholder}")
    private String serviceRoleKey;

    /**
     * Get Supabase URL
     */
    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    /**
     * Get Anon Key (public key for client-side operations)
     */
    public String getAnonKey() {
        return anonKey;
    }

    /**
     * Get Service Role Key (secret key for backend operations)
     */
    public String getServiceRoleKey() {
        return serviceRoleKey;
    }

    /**
     * WebClient bean for REST API calls to Supabase
     */
    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", anonKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * WebClient bean for backend operations (with service role key)
     */
    @Bean
    public WebClient supabaseBackendClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", serviceRoleKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
