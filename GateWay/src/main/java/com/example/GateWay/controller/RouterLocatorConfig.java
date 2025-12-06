package com.example.GateWay.controller;

import com.example.GateWay.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouterLocatorConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public RouteLocator routerLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/product-api/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://product-service"))

                .route("user-service", r -> r
                        .path("/users-api/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://user-service"))

                .route("order-service", r -> r
                        .path("/order-api/products/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("http://order-service"))
                .build();
    }
}
