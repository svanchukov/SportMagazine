package ru.svanchukov.user.User_Service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.LoginRequestDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.AuthorizationService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Регистрация и вход пользователей")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateNewUserDTO createNewUserDTO) {
        return ResponseEntity.ok(authorizationService.register(createNewUserDTO));
    }

    @Operation(summary = "Вход (логин) пользователя")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authorizationService.login(loginRequestDTO));
    }
}


