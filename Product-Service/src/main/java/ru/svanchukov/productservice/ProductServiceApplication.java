package ru.svanchukov.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Основной класс приложения ProductService.
 * <p>
 * Запускает микросервис, включает возможность выполнения задач по расписанию
 * и регистрирует сервис в системе Service Discovery.
 */
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class ProductServiceApplication {

    public ProductServiceApplication() {
        // пустой конструктор по умолчанию
    }

    /**
     * Точка входа в приложение.
     *
     */
    public static void main(final String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    /**
     * Создаёт и настраивает бин {@link WebClient.Builder}.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }


}
