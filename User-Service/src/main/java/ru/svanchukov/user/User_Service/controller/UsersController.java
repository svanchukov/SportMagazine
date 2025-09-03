package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UsersService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления списком пользователей.
 * Поддерживает отображение списка пользователей, создание новых пользователей и удаление существующих.
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);
    private final UsersService usersService;

    /**
     * Отображает список всех пользователей.
     * @param name  необязательный параметр фильтра по имени
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы со списком пользователей
     */
    @GetMapping
    public String getUserList(@RequestParam(required = false) final String name, final Model model) {
        LOGGER.info("Запрос на получение списка пользователей");
        final List<UserDTO> users = usersService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("name", name);
        return "users";
    }

    /**
     * Отображает форму для создания нового пользователя.
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы создания пользователя
     */
    @GetMapping("/new")
    public String getNewUserPage(final Model model) {
        LOGGER.info("Показ формы создания пользователя");
        model.addAttribute("createNewUserDTO", new CreateNewUserDTO());
        return "new";
    }

    /**
     * Создает нового пользователя.
     * @param createNewUserDTO DTO с данными нового пользователя
     * @param bindingResult    результат валидации
     * @param model            модель для передачи ошибок
     * @return редирект на список пользователей или форма создания при ошибках
     */
    @PostMapping
    public String createUser(@Valid @ModelAttribute("createNewUserDTO") final CreateNewUserDTO createNewUserDTO,
                             final BindingResult bindingResult,
                             final Model model) {
        LOGGER.info("Создание нового пользователя: {}", createNewUserDTO);

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Ошибки валидации при создании пользователя");
            model.addAttribute("createNewUserDTO", createNewUserDTO);
            return "new";
        }

        try {
            usersService.saveUser(createNewUserDTO);
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Ошибка при создании пользователя: {}", e.getMessage());
            }
            model.addAttribute("error", "Произошла ошибка: " + e.getMessage());
            return "new";
        }
    }

    /**
     * Удаляет пользователя по ID.
     * @param userId UUID пользователя
     * @return редирект на страницу со списком пользователей
     */
    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable final UUID userId) {
        LOGGER.info("Удаление пользователя с ID: {}", userId);
        usersService.deleteUser(userId);
        return "redirect:/users";
    }
}
