package ru.svanchukov.productservice.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений в приложении.
 * Перехватывает все {@link Exception} и перенаправляет пользователя
 * на страницу с ошибкой.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все необработанные исключения
     * model модель для передачи данных в представление.
     */
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
