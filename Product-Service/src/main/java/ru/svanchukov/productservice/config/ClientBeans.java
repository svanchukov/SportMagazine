package ru.svanchukov.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.svanchukov.productservice.client.RestClientProductRestClientImpl;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientProductRestClientImpl productRestClient(
            @Value("${svanchukov.services.order.uri:http://localhost:8081}") String orderBaseUri) {
        return new RestClientProductRestClientImpl(RestClient.builder()
                .baseUrl(orderBaseUri)
                .build());
    }
}
