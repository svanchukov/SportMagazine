package ru.svanchukov.user.User_Service.handler;

/**
 * Исключение выбрасывается, когда пользователь с указанным идентификатором не найден.
 */
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(final String message) {
        super(message);
    }
}
