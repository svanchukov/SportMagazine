package ru.svanchukov.productservice.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.svanchukov.productservice.security.CustomAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Фильтр для аутентификации запросов на основе JWT.
 * <p>
 * Извлекает токен из заголовка Authorization или параметра запроса,
 * валидирует его и помещает пользователя в SecurityContextHolder.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Конструктор фильтра.
     * @param jwtUtil           утилита для работы с JWT
     * @param userDetailsService сервис для загрузки данных пользователя
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Основная логика фильтра.
     * Извлекает JWT из запроса, валидирует его и добавляет аутентификацию в контекст.
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @param chain    цепочка фильтров
     * @throws ServletException если ошибка фильтрации
     * @throws IOException      если ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        String authHeader = request.getHeader("Authorization");

        LOGGER.info("Request URL: {}?{}", request.getRequestURL(), request.getQueryString());
        LOGGER.debug("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(BEARER_PREFIX_LENGTH);
            LOGGER.debug("Token from header: {}", token);
        }

        if (token == null) {
            token = request.getParameter("token");
            LOGGER.debug("Token from parameter: {}", token);
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            LOGGER.info("Username from token: {}", username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                LOGGER.info("UserDetails loaded: {}", userDetails.getUsername());

                CustomAuthenticationToken authentication = new CustomAuthenticationToken(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.debug("Authentication set with principal: {}", authentication.getPrincipal());

            } catch (Exception e) {
                LOGGER.error("Failed to load UserDetails for username {}: {}", username, e.getMessage());
                chain.doFilter(request, response);
                return;
            }
        } else {
            LOGGER.warn("Invalid or missing token: {}", token);
        }

        chain.doFilter(request, response);
    }
}
