package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UserService;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Контроллер для управления операциями над конкретным пользователем.
 * Поддерживает просмотр деталей, редактирование и удаление пользователя.
 */
@Controller
@RequestMapping("/user/{userId:[0-9a-fA-F\\-]{36}}")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Загружает пользователя по ID перед каждым запросом и добавляет его в модель.
     * @param userId UUID пользователя
     * @return DTO пользователя
     */
    @ModelAttribute("user")
    public UserDTO getUser(@PathVariable("userId") final UUID userId) {
        LOGGER.info("Загрузка пользователя по ID: {}", userId);
        return userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));
    }

    /**
     * Отображает страницу с деталями пользователя.
     * @param userId UUID пользователя
     * @param model  модель для передачи данных в представление
     * @return имя шаблона страницы деталей пользователя
     */
    @GetMapping("/details")
    public String getUserDetails(@PathVariable final UUID userId, final Model model) {
        LOGGER.info("Загрузка деталей пользователя с ID: {}", userId);
        final UserDTO user = userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));
        model.addAttribute("user", user);
        return "user-details";
    }

    /**
     * Отображает страницу редактирования пользователя.
     * @param userId UUID пользователя
     * @param model  модель для передачи данных в представление
     * @return имя шаблона страницы редактирования
     */
    @GetMapping("/edit")
    public String redirectToEdit(@PathVariable final UUID userId, final Model model) {
        LOGGER.info("Переход на редактирование пользователя с ID: {}", userId);
        final UpdateUserDTO updateUserDTO = userService.getUpdateUserDTO(userId);
        model.addAttribute("updateUserDTO", updateUserDTO);
        return "edit";
    }

    /**
     * Обновляет данные пользователя.
     * @param userId         UUID пользователя
     * @param updateUserDTO  DTO с обновлёнными данными
     * @param bindingResult  результат валидации
     * @param model          модель для передачи ошибок
     * @return редирект на страницу деталей или форма редактирования при ошибках
     */
    @PostMapping("/edit")
    public String updateUser(@PathVariable final UUID userId,
                             @Valid @ModelAttribute("updateUserDTO") final UpdateUserDTO updateUserDTO,
                             final BindingResult bindingResult,
                             final Model model) {
        LOGGER.info("Обновление пользователя с ID: {}", userId);

        if (bindingResult.hasErrors()) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Ошибка валидации: {}", bindingResult.getAllErrors());
            }
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit";
        }

        try {
            userService.updateUser(userId, updateUserDTO);
            return "redirect:/user/" + userId + "/details";
        } catch (NoSuchElementException | IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            }
            model.addAttribute("error", e.getMessage());
            return "edit";
        }
    }

    /**
     * Удаляет пользователя по ID.
     * @param userId UUID пользователя
     * @return редирект на страницу со списком пользователей
     */
    @PostMapping("/delete")
    public String deleteUser(@PathVariable final UUID userId) {
        LOGGER.info("Удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
        return "redirect:/users";
    }

    /**
     * Обрабатывает все исключения, возникшие в контроллере.
     * @param ex исключение
     * @param model модель для передачи информации об ошибке
     * @return имя шаблона страницы ошибки
     */
    @ExceptionHandler(Exception.class)
    public String handleException(final Exception ex, final Model model) {
        model.addAttribute("error", "Произошла ошибка: " + ex.getMessage());
        return "error";
    }
}
