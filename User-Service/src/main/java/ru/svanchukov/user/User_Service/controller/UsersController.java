package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UserService;
import ru.svanchukov.user.User_Service.service.UsersService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @GetMapping
    public String getUserList(@RequestParam(required = false) String name, Model model) {
        logger.info("Запрос на получение списка пользователей");
        List<UserDTO> users = usersService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("name", name);
        return "users";
    }

    @GetMapping("/new")
    public String getNewUserPage(Model model) {
        logger.info("Показ формы создания пользователя");
        model.addAttribute("createNewUserDTO", new CreateNewUserDTO());
        return "new";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("createNewUserDTO") CreateNewUserDTO createNewUserDTO,
                             BindingResult bindingResult,
                             Model model) {
        logger.info("Создание нового пользователя: {}", createNewUserDTO);

        if (bindingResult.hasErrors()) {
            logger.warn("Ошибки валидации при создании пользователя");
            model.addAttribute("createNewUserDTO", createNewUserDTO);
            return "new";
        }

        try {
            usersService.saveUser(createNewUserDTO);
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage());
            model.addAttribute("error", "Произошла ошибка: " + e.getMessage());
            return "new";
        }
    }


    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable UUID userId) {
        logger.info("Удаление пользователя с ID: {}", userId);
        usersService.deleteUser(userId);  // Удаляем пользователя
        return "redirect:/users";  // Перенаправляем на список пользователей
    }

}
