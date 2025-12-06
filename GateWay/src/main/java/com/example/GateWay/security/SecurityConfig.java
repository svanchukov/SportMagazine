package com.example.GateWay.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {


        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((swe, e) -> {
                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return Mono.empty();
                        })
                )
                .authorizeExchange(ex -> ex
                                .pathMatchers("/auth/**").permitAll()
                                .pathMatchers("/swagger/**").permitAll()
                                .pathMatchers("/v3/api-docs/**").permitAll()
                                .pathMatchers("/webjars/**").permitAll()
                                .pathMatchers("/swagger-ui/**").permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}