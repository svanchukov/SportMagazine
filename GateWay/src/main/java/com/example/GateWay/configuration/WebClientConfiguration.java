package com.example.GateWay.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@org.springframework.context.annotation.Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient userWebClient(@Value("${user-service.url}") String userServiceURl) {
        return WebClient.builder()
                .baseUrl(userServiceURl)
                .build();
    }
}
