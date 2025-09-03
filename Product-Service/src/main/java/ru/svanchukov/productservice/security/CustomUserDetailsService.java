package ru.svanchukov.productservice.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Кастомная реализация UserDetailsService для Spring Security.
 * <p>
 * Используется для загрузки данных о пользователе по его email.
 * В текущей реализации возвращается заглушка с пустым паролем и без ролей.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new User(email, "", Collections.emptyList()); // Временная заглушка
    }
}