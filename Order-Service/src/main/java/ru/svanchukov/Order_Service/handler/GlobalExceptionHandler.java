package ru.svanchukov.Order_Service.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибки PriceException.
     */
    @ExceptionHandler(PriceException.class)
    public ResponseEntity<String> handlePriceException(PriceException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    /**
     * Обработка ошибки при сохранении продукта.
     */
    @ExceptionHandler(ProductSavingException.class)
    public ResponseEntity<Map<String, Object>> handleProductSavingException(ProductSavingException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Database error");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation error");

        // достаём все ошибки валидации
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }

}
