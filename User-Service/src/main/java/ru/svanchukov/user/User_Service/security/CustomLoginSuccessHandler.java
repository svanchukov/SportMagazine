package ru.svanchukov.user.User_Service.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.svanchukov.user.User_Service.security.jwt.JwtUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Обработчик успешной аутентификации пользователя.
 * Генерирует JWT-токен и перенаправляет пользователя на нужную страницу с токеном в URL.
 */
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLoginSuccessHandler.class);

    private final JwtUtil jwtUtil;

    /**
     * Метод вызывается после успешной аутентификации.
     * @param request        HTTP-запрос
     * @param response       HTTP-ответ
     * @param authentication объект аутентификации
     * @throws IOException      в случае ошибки ввода/вывода
     * @throws ServletException в случае ошибки сервлета
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {
        final String email = authentication.getName();
        final String token = jwtUtil.generateToken(email);

        LOGGER.info("Пользователь '{}' успешно аутентифицирован. Сгенерирован токен: {}", email, token);

        // Кодируем токен для безопасной передачи в URL
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

        // Перенаправляем с токеном в URL
        final String redirectUrl = "http://localhost:8080/products?token=" + encodedToken;
        LOGGER.debug("Перенаправление на URL: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
