package ru.svanchukov.Order_Service.controller;

import ru.svanchukov.Order_Service.dto.ProductDTO;
import ru.svanchukov.Order_Service.dto.UpdateProductDTO;
import ru.svanchukov.Order_Service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/order-api/products")
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "REST-контроллер для управления продуктами")
public class ProductController {

    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate; // Добавляем KafkaTemplate
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private static final String TOPIC = "product-events";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Получение продукта по ID", description = "Возвращает данные продукта по ID")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        logger.info("Запрос на получение продукта с ID: {}", productId);
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchElementException("Продукт с ID " + productId + " не найден"));
    }

    @Operation(summary = "Обновление продукта по ID", description = "Обновляет данные продукта  ")
    @PatchMapping("/{productId}")
    public ResponseEntity<UpdateProductDTO> updateProduct(@PathVariable Long productId,
                                                          @Valid @RequestBody Map<String, Object> updates) {
        logger.info("Запрос на обновление продукта с ID: {}", productId);
        productService.partialUpdate(productId, updates);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление продукта по ID", description = "Удаляет продукт по ID")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        logger.info("Запрос на удаление продукта с ID: {}", productId);
        // Отправка события в Kafka
        try {
            productService.delete(productId);
            String message = "Продукт с ID " + productId + " удален";
            kafkaTemplate.send(TOPIC, "delete-product-" + productId, message);
            logger.info("Отправлено сообщение в Kafka: топик={}, ключ={}, сообщение={}", TOPIC, "delete-product-" + productId, message);
        } catch (Exception e) {
            String errorMessage = "Продукт с ID: " + productId + "не найден для удаления";
            kafkaTemplate.send(TOPIC, "delete-product-error-" + productId, errorMessage);
            logger.error("Отправлено сообщение об ошибке в Kafka: топик={}, ключ={}, сообщение={}", TOPIC, "delete-product-error-" + productId, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("test-kafka-error")
    public ResponseEntity<Void> testKafkaError() {
        kafkaTemplate.send(TOPIC, "test-error-message");
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}