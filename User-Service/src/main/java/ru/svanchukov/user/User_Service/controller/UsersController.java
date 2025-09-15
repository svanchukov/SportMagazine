package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UsersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления списком пользователей.
 */
@RestController
@RequestMapping("/users-api/users")
@RequiredArgsConstructor
@Tag(name = "UsersController API", description = "Операции для управления списком пользователей")
public class UsersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);
    private final UsersService usersService;

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUserList() {
        LOGGER.info("Запрос на получение списка пользователей");
        final List<UserDTO> users = usersService.findAll();
        if (users.isEmpty()) {
            LOGGER.info("Список пользователей пуст");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody final CreateNewUserDTO createNewUserDTO,
            final BindingResult bindingResult) {

        LOGGER.info("Создание нового пользователя: {}", createNewUserDTO);

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Ошибки валидации при создании пользователя");
            return ResponseEntity.badRequest().build();
        }

        try {
            UserDTO createdUser = usersService.saveUser(createNewUserDTO);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LOGGER.error("Неожиданная ошибка при создании пользователя", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Удалить пользователя по ID")
    @PostMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") final UUID userId) {

        LOGGER.info("Удаление пользователя с ID: {}", userId);
        usersService.deleteUser(userId);
        LOGGER.info("Пользователь с ID: {}, удалён", userId);
        return ResponseEntity.noContent().build();
    }
}
