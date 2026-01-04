package com.onclass.bootcamp.application.config;

import com.onclass.bootcamp.application.util.WebClientConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(WebClientConstants.CAPACIDAD_SERVICE_BASE_URL)
                .build();
    }
}