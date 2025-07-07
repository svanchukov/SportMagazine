package ru.svanchukov.productservice.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails userDetails;

    // Конструктор принимает только UserDetails
    public CustomAuthenticationToken(UserDetails userDetails) {
        super(Collections.emptyList()); // Пустой список авторизаций, так как роли не используются
        this.userDetails = userDetails;
        setAuthenticated(true); // Устанавливаем, что токен аутентифицирован
    }

    @Override
    public Object getCredentials() {
        return null; // Нет пароля в данном контексте
    }

    @Override
    public Object getPrincipal() {
        return this.userDetails; // Возвращаем имя пользователя
    }
}
