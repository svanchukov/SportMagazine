package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UserService;

import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/user/{userId:[0-9a-fA-F\\-]{36}}")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Получаем пользователя по ID для использования в других методах
    @ModelAttribute("user")
    public UserDTO getUser(@PathVariable("userId") UUID userId) {
        logger.info("Загрузка пользователя по ID: {}", userId);
        return userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));
    }

    @GetMapping("/details")
    public String getUserDetails(@PathVariable UUID userId, Model model) {
        logger.info("Загрузка деталей пользователя с ID: {}", userId);

        UserDTO user = userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));
        model.addAttribute("user", user);

        return "user-details"; // Показываем страницу с деталями пользователя
    }

    // Отображение деталей пользователя
    @GetMapping("/edit")
    public String redirectToEdit(@PathVariable UUID userId, Model model) {
        logger.info("Переход на редактирование пользователя с ID: {}", userId);

        UpdateUserDTO updateUserDTO = userService.getUpdateUserDTO(userId);
        model.addAttribute("updateUserDTO", updateUserDTO);

        return "edit"; // сразу показываем страницу редактирования
    }

    // Обновление пользователя
    @PostMapping("/edit")
    public String updateUser(@PathVariable UUID userId,
                             @Valid @ModelAttribute("updateUserDTO") UpdateUserDTO updateUserDTO,
                             BindingResult bindingResult,
                             Model model) {
        logger.info("Обновление пользователя с ID: {}", userId);

        if (bindingResult.hasErrors()) {
            logger.error("Ошибка валидации: {}", bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit";
        }

        try {
            userService.updateUser(userId, updateUserDTO);
            return "redirect:/user/" + userId + "/details";
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "edit";
        }
    }

    // Удаление пользователя
    @PostMapping("/delete")
    public String deleteUser(@PathVariable UUID userId) {
        logger.info("Удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
        return "redirect:/users"; // Перенаправляем на список пользователей
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("error", "Произошла ошибка: " + ex.getMessage());
        return "error";
    }
}
