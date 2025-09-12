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
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        LOGGER.info("Пользователь '{}' успешно аутентифицирован.", email);

        // Генерация токена
        String token = jwtUtil.generateToken(email);

        // Вариант 1: отдать токен в заголовке (лучше для SPA/REST)
        response.setHeader("Authorization", "Bearer " + token);

        // Вариант 2: редирект с токеном в query (менее безопасно, но наглядно)
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect("/products?token=" + encodedToken);
    }

}
