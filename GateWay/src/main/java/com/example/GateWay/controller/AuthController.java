package com.example.GateWay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Регистрация и вход через Gateway")
public class AuthController {

    private final WebClient webClient;


    @Operation(summary = "Вход пользователя через Gateway")
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody Object userDto) {
        return webClient.post()
                .uri("/users/login")
                .bodyValue(userDto)
                .retrieve()
                .toEntity(String.class);
    }

    @Operation(summary = "Регистрация пользователя через Gateway")
    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody Object userDto) {
        return webClient.post()
                .uri("/users/register")
                .bodyValue(userDto)
                .retrieve()
                .toEntity(String.class);
    }
}
