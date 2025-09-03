package ru.svanchukov.Order_Service.restTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс для создания бина {@link RestTemplate}.
 * Позволяет внедрять {@link RestTemplate} в сервисы для выполнения HTTP-запросов.
 */
@Configuration
public final class RestTemplateConfig {

    private RestTemplateConfig() {
    }

    /**
     * Создаёт и возвращает бин {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
