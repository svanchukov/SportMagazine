package com.example.GateWay.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("JwtFilter")
@RequiredArgsConstructor
public class JwtFilter extends AbstractGatewayFilterFactory<Object> {

    private final JwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // публичные пути — не проверяем
            if (path.startsWith("/auth/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            ServerHttpResponse response = exchange.getResponse();

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(response.bufferFactory()
                        .wrap("{\"error\":\"Нет заголовка Authorization\"}".getBytes())));
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(response.bufferFactory()
                        .wrap("{\"error\":\"Недействительный или просроченный токен\"}".getBytes())));
            }

            String email = jwtUtil.extractEmail(token);

            ServerHttpRequest mutated = exchange.getRequest()
                    .mutate()
                    .header("X-User-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }
}
