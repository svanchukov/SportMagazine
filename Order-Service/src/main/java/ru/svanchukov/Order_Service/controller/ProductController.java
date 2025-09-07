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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST-контроллер для управления определенными продуктами по id в Order Service.
 * Предоставляет операции получения, обновления и удаления продукта.
 */
@RestController
@RequestMapping("/order-api/products")
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "REST-контроллер для управления продуктами")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);


    private final ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Получение продукта по его идентификатору.
     */
    @Operation(summary = "Получение продукта по ID", description = "Возвращает данные продукта по ID")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable final Long productId) {
        LOGGER.info("Запрос на получение продукта с ID: {}", productId);
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchElementException("Продукт с ID " + productId + " не найден"));
    }

    /**
     * Частичное обновление продукта по ID.
     */
    @Operation(summary = "Обновление продукта по ID", description = "Обновляет данные продукта")
    @PatchMapping("/{productId}")
    public ResponseEntity<UpdateProductDTO> updateProduct(@PathVariable final Long productId,
                                                          @Valid @RequestBody final Map<String, Object> updates) {
        LOGGER.info("Запрос на обновление продукта с ID: {}", productId);
        productService.partialUpdate(productId, updates);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление продукта по ID.
     */
    @Operation(summary = "Удаление продукта по ID", description = "Удаляет продукт по ID")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long productId) {
        LOGGER.info("Запрос на удаление продукта с ID: {}", productId);
        try {
            productService.delete(productId);
        } catch (Exception e) {
            LOGGER.warn("Продукт с ID {} не найден для удаления", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Глобальный обработчик исключений для случаев, когда элемент не найден.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(final NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
