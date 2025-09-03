package ru.svanchukov.productservice.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

/**
 * Кастомная реализация токена аутентификации для Spring Security.
 * Используется для хранения информации о пользователе без учёта пароля
 * (например, при аутентификации по JWT).
 */
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails userDetails;

    /**
     * Создаёт токен аутентификации для указанного пользователя.
     *
     * @param userDetails детали аутентифицированного пользователя
     */
    // Конструктор принимает только UserDetails
    public CustomAuthenticationToken(UserDetails userDetails) {
        super(Collections.emptyList()); // Пустой список авторизаций, так как роли не используются
        this.userDetails = userDetails;
        setAuthenticated(true); // Устанавливаем, что токен аутентифицирован
    }

    /**
     * В данном токене нет пароля, поэтому возвращает {@code null}.
     *
     * @return всегда {@code null}
     */
    @Override
    public Object getCredentials() {
        return null; // Нет пароля в данном контексте
    }

    /**
     * Возвращает детали аутентифицированного пользователя.
     *
     * @return объект {@link UserDetails}
     */
    @Override
    public Object getPrincipal() {
        return this.userDetails; // Возвращаем имя пользователя
    }
}
