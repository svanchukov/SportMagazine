package ru.svanchukov.user.User_Service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
     *
     * @param http                      объект конфигурации HttpSecurity
     * @param customLoginSuccessHandler кастомный обработчик успешной аутентификации
     * @return SecurityFilterChain
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomLoginSuccessHandler customLoginSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // Статические ресурсы
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/users", "/users/new", "/login", "/register").permitAll()

                        // Actuator health и info - для всех
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

                        // Остальные actuator endpoints - только для аутентифицированных пользователей
                        .requestMatchers("/actuator", "/actuator/**").hasRole("ADMIN")

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                // остальная конфигурация без изменений
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/actuator/health", "/actuator/health/**")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}