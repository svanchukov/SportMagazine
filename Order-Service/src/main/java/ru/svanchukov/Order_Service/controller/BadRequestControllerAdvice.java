package ru.svanchukov.Order_Service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;

import java.util.Locale;
import java.util.Optional;

/**
 * Глобальный обработчик исключений для контроллеров Order Service.
 * Обрабатывает ошибки валидации запросов и формирует подробный ответ с HTTP статусом 400 (Bad Request).
 */
@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    /** Источник сообщений для локализации ошибок */
    private final MessageSource messageSource;

    /**
     * Обрабатывает исключения, возникающие при валидации входящих данных.
     *
     * @param exception исключение BindException
     * @param locale    локаль для локализации сообщений
     * @return ResponseEntity с объектом ProblemDetail и статусом 400
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception, Locale locale) {
        Locale resolvedLocale = (locale != null) ? locale : Locale.getDefault();

        // Создаем ProblemDetail с Optional
        ProblemDetail problemDetail = Optional.ofNullable(
                        messageSource.getMessage("errors.400.title", null, "Bad Request", resolvedLocale))
                .map(title -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, title))
                .orElse(ProblemDetail.forStatus(HttpStatus.BAD_REQUEST));

        // Добавляем ошибки валидации
        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
                        .map(error -> {
                            return messageSource.getMessage(String.valueOf(error), new String[]{error.getDefaultMessage()}, resolvedLocale);
                        })
                        .toList()
        );


        return ResponseEntity.badRequest().body(problemDetail);
    }
}
