package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Provides shared beans used across all integration tests:
 * - RestClient pointing to the local application
 */
@TestConfiguration
public class TestConfig {

        @Value("${api.base-url}")
    private String apiBaseUrl;

    /**
     * RestClient pointing to the local application.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
