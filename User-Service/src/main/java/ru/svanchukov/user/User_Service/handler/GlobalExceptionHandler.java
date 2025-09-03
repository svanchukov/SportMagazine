package ru.svanchukov.user.User_Service.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Перехватывает исключения и перенаправляет на страницу с ошибкой.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {

    }

    /**
     * Обрабатывает все необработанные исключения.
     * @param e     исключение
     * @param model модель для передачи данных в представление
     * @return имя HTML-шаблона страницы ошибки
     */
    @ExceptionHandler(Exception.class)
    public String handleError(final Exception e, final Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
