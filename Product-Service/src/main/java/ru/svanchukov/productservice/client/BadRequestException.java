package ru.svanchukov.productservice.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException {

    private final List<String> error;

    public BadRequestException(List<String> error) {
        this.error = error;
    }

    public BadRequestException(String message, List<String> error) {
        super(message);
        this.error = error;
    }

    public BadRequestException(String message, Throwable cause, List<String> error) {
        super(message, cause);
        this.error = error;
    }

    public BadRequestException(Throwable cause, List<String> error) {
        super(cause);
        this.error = error;
    }

    public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<String> error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.error = error;
    }
}