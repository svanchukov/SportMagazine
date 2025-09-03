package ru.svanchukov.Order_Service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;

import java.util.Locale;

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
     * Обрабатывает исключения BindException, возникающие при валидации входящих данных.
     * Формирует объект {@link ProblemDetail} с подробной информацией об ошибках.
     *
     * @param exception исключение BindException с информацией о ошибках валидации
     * @param locale    локаль для локализации сообщений об ошибках
     * @return ResponseEntity с {@link ProblemDetail} и HTTP статусом 400 (Bad Request)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title",
                                new Object[0],
                                "errors.400.title", locale));
        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
                        .map(MessageSourceResolvable::getDefaultMessage)
                        .toList());

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }
}
