package ru.svanchukov.user.User_Service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для обработки запросов на страницу авторизации пользователя.
 * Отвечает за отображение формы логина.
 */
@Controller
public class LoginController {

    public LoginController() {

    }

    /**
     * Отображает страницу логина.
     * @return имя HTML-шаблона страницы логина (login.html)
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Thymeleaf ищет login.html в папке /templates
    }
}
