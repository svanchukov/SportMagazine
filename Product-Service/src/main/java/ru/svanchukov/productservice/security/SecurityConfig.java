package ru.svanchukov.productservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.svanchukov.productservice.security.jwt.JwtAuthenticationFilter;
import ru.svanchukov.productservice.security.jwt.JwtUtil;

/**
    Конфигурация безопасности для {@code ProductService}.
    Класс определяет правила авторизации, настройки аутентификации и фильтры Spring Security.
    Использует JWT и классическую форму входа.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ProductServiceSuccessHandler successHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
        Создаёт новый экземпляр конфигурации безопасности.
     */
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService,
                          ProductServiceSuccessHandler successHandler, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Конфигурирует {@link SecurityFilterChain} для приложения.
     * Показывает какие точки входа разрешены после выполнения определенных проверок.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)

                // правила авторизации
                .authorizeHttpRequests()
                .requestMatchers("/products/**").authenticated()
                .requestMatchers("/login").permitAll()
                .anyRequest().permitAll()

                // форма логина (для тестов через браузер)
                .and()
                .formLogin()
                .loginPage("http://localhost:8083/login")
                .successHandler(successHandler)
                .permitAll()
                .and()

                // обработка ошибок
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()

                // логаут
                .logout()
                .permitAll()
                .and()

                // отключение CSRF
                .csrf().disable();

        return http.build();
    }
}