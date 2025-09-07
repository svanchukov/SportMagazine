package ru.svanchukov.productservice.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Исключение, которое выбрасывается при ошибке 400 (Bad Request).
 * Хранит список сообщений об ошибках.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final List<String> error;

    public BadRequestException(final List<String> error) {
        super();
        this.error = error;
    }

    public BadRequestException(final String message, final List<String> error) {
        super(message);
        this.error = error;
    }

    public BadRequestException(final String message, final Throwable cause, final List<String> error) {
        super(message, cause);
        this.error = error;
    }

    public BadRequestException(final Throwable cause, final List<String> error) {
        super(cause);
        this.error = error;
    }

    public BadRequestException(final String message,
                               final Throwable cause,
                               final boolean enableSuppression,
                               final boolean writableStackTrace,
                               final List<String> error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.error = error;
    }
}