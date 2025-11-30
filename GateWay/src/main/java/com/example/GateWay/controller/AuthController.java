package com.example.GateWay.controller;

import com.example.GateWay.dto.LoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final WebClient.Builder webClient;

    public Mono<String> login(@RequestBody LoginRequestDTO dto) {
        return webClient
                .baseUrl("http://localhost:8083")
                .build()
                .post()
                .uri("/users/login")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class);
    }
}
