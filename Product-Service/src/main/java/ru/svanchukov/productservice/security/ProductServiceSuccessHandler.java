package ru.svanchukov.productservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Обработчик успешной аутентификации для {@code ProductService}.
 * Данный класс реализует {@link AuthenticationSuccessHandler} и выполняет следующие действия:
 *     Извлекает имя аутентифицированного пользователя из объекта {@link Authentication}.
 *     Логирует факт успешной аутентификации.
 *     Выполняет перенаправление пользователя на страницу {@code /products}.
 */
@Component
public class ProductServiceSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceSuccessHandler.class);


    /**
        Вызывается Spring Security после успешной аутентификации пользователя.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        String username = principal instanceof org.springframework.security.core.userdetails.UserDetails
                ? ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername()
                : principal.toString();
        LOGGER.info("Authenticated user: " + username);
        response.sendRedirect("/products");
    }
}
