package com.example.GateWay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        // login и logout доступны всем
                        .pathMatchers("/login", "/logout").permitAll()
                        // Swagger и API через gateway требуют авторизации
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler())
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("gateway")
                .password("{noop}12345")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}

