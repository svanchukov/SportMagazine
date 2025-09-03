package ru.svanchukov.user.User_Service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.svanchukov.user.User_Service.handler.SecurityConfigurationException;

/**
 * Конфигурация безопасности приложения.
 * Настраивает доступ к страницам, аутентификацию и выход из системы.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {

    }

    /**
     * Настраивает цепочку фильтров безопасности Spring Security.
     * @param http                     объект конфигурации HttpSecurity
     * @param customLoginSuccessHandler кастомный обработчик успешной аутентификации
     * @return SecurityFilterChain
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
                                                   final CustomLoginSuccessHandler customLoginSuccessHandler) {
        try {
            http
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/users", "/users/new", "/login").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin((form) -> form
                            .loginPage("/login")
                            .successHandler(customLoginSuccessHandler) // Используем кастомный обработчик
                            .permitAll()
                    )
                    .logout(LogoutConfigurer::permitAll)
                    .csrf().disable();
            return http.build();
        } catch (Exception e) {
            throw new SecurityConfigurationException("Ошибка конфигурации Spring Security", e);
        }

    }

    /**
     * Настройка кодировщика паролей.
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Временно отключаю хеширование
    }
}