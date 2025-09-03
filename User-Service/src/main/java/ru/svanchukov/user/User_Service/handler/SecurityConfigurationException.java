package ru.svanchukov.user.User_Service.handler;

/**
 * Кастомное исключение для ошибок конфигурации Spring Security.
 * Используется в SecurityConfig при ошибках настройки цепочки фильтров.
 */
public class SecurityConfigurationException extends RuntimeException {
    public SecurityConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
