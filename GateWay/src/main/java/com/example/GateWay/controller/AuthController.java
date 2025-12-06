package com.example.GateWay.controller;

import com.example.GateWay.dto.LoginRequestDTO;
import com.example.GateWay.dto.TokenResponseDTO;
import com.example.GateWay.jwt.JwtUtil;
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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private final WebClient userClient = WebClient.builder()
            .baseUrl("http://localhost:8083")
            .build();

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return userClient.post()
                .uri("/users-api/users/validate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(resp -> {
                    Object userId = resp.get("userId");
                    if (userId == null) {
                        return Mono.error(new RuntimeException("User ID not refused"));
                    }

                    String token = jwtUtil.generateAccessToken(
                            Map.of(),
                            userId.toString()
                    );

                    return Mono.just(new TokenResponseDTO(token));
                });
    }
}
