package ru.svanchukov.user.User_Service.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.NoSuchElementException;

/**
 * Контроллер для управления операциями над конкретным пользователем.
 */
@RestController
@RequestMapping("/users-api/users/{userId:\\d+}")
@Tag(name = "UserController API", description = "Операции над конкретным пользователем")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") Long userId) {

        LOGGER.info("Загрузка пользователя по ID: {}", userId);
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    @Operation(summary = "Получить детали пользователя")
    @GetMapping("/details")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable("userId") Long userId) {

        LOGGER.info("Загрузка деталей пользователя с ID: {}", userId);
        final UserDTO user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Обновить данные пользователя")
    @PatchMapping("/edit")
    public ResponseEntity<UpdateUserDTO> updateUser(@PathVariable("userId") Long userId,
            @Valid @RequestBody final UpdateUserDTO updateUserDTO,
            final BindingResult bindingResult) {

        LOGGER.info("Обновление пользователя с ID: {}", userId);

        if (bindingResult.hasErrors()) {
            LOGGER.error("Ошибка валидации: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().build();
        }

        try {
            UpdateUserDTO updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            LOGGER.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить пользователя по ID")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {

        LOGGER.info("Удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
        LOGGER.info("Пользователь с ID: {}, удалён", userId);
        return ResponseEntity.noContent().build();
    }

}
