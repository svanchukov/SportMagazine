package ru.svanchukov.Order_Service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.svanchukov.Order_Service.dto.CreateNewProductDTO;
import ru.svanchukov.Order_Service.dto.ProductDTO;

import java.io.IOException;
import java.util.Map;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendProductUpdateEvent(Long productId, Map<String, Object> updates) {
        try {
            String message = objectMapper.writeValueAsString(updates);
            kafkaTemplate.send(TOPIC, "update-product-id " + productId, message);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправлении сообщения в Kafka", e);
        }
    }

    public void sendProductDeleteEvent(Long productId) {
        String message = "Продукт с ID: " + productId + " удалён";
        kafkaTemplate.send("product-events", "delete-product- " + productId, message);
    }

    public void sendProductDeleteErrorEvent(Long productId) {
        String message = "Ошибка при удалении продукта с ID " + productId;
        kafkaTemplate.send("product-events-DLT", "delete-product-error-" + productId, message);
    }

    public void sendProductCreateEvent(Long productId, CreateNewProductDTO createNewProductDTO) {
        try {
            String message = objectMapper.writeValueAsString(createNewProductDTO);
            kafkaTemplate.send("product-events", "create-product- " + productId, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при отправке события о создании продукта в Kafka", e);
        }
    }
}

























