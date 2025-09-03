package ru.svanchukov.Order_Service.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для контроллеров.
 * Перехватывает исключения и отображает страницу с сообщением об ошибке.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все исключения типа {@link Exception}.
     */
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
