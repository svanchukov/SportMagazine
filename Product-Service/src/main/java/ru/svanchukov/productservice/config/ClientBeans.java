package ru.svanchukov.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.svanchukov.productservice.client.RestClientProductRestClientImpl;

/**
 * Конфигурационный класс для создания бинов клиентов.
 * <p>
 * Здесь регистрируется {@link RestClientProductRestClientImpl}, который используется
 * для взаимодействия с сервисом заказов через HTTP.
 */
@Configuration
public class ClientBeans {

    public ClientBeans() {
        // Конструктор по умолчанию (нужен для Spring и для PMD)
    }

    /**
     * Создает бин {@link RestClientProductRestClientImpl}, который используется
     * для взаимодействия с сервисом заказов (Order-Service).
     */
    @Bean
    public RestClientProductRestClientImpl productRestClient(
            @Value("${svanchukov.services.order.uri:http://localhost:8081}") final String orderBaseUri) {
        return new RestClientProductRestClientImpl(RestClient.builder()
                .baseUrl(orderBaseUri)
                .build());
    }

}
