package ru.svanchukov.productservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Кастомная точка входа для обработки ошибок аутентификации в Spring Security.
 * <p>
 * Если пользователь не авторизован или сессия недействительна,
 * то выполняется перенаправление на страницу логина.
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    /**
     * Метод вызывается Spring Security при ошибке аутентификации.
     * @param request       текущий HTTP-запрос
     * @param response      текущий HTTP-ответ
     * @param authException исключение, связанное с неудачной аутентификацией
     * @throws IOException      при ошибке ввода/вывода
     * @throws ServletException при ошибке работы с сервлетом
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        LOGGER.info("Authentication failed: " + authException.getMessage());
        response.sendRedirect("http://localhost:8083/login");
    }
}