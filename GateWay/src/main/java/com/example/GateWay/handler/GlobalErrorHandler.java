package com.example.GateWay.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(100)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = """
                {
                    "error": "Gateway error",
                    "message": "%s"
                }
                """.formatted(ex.getMessage());

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(json.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
