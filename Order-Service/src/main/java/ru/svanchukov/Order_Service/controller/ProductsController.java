package ru.svanchukov.Order_Service.controller;

import ru.svanchukov.Order_Service.dto.CreateNewProductDTO;
import ru.svanchukov.Order_Service.dto.ProductDTO;
import ru.svanchukov.Order_Service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления списком продуктов в Order Service.
 * Предоставляет операции получения всех продуктов, создания нового продукта и поиска по имени.
 */
@RestController
@RequestMapping("/order-api/products")
@RequiredArgsConstructor
@Tag(name = "ProductsController", description = "REST-контроллер для управления списком продуктов")
public class ProductsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    private final ProductService productService;

    /**
     * Получение списка всех продуктов.
     */
    @Operation(summary = "Получение всех продуктов", description = "Возвращает список всех продуктов")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProductsList() {
        LOGGER.info("Запрос на получение всех продуктов");
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * Создание нового продукта.
     */
    @Operation(summary = "Создание нового продукта", description = "Создаёт новый продукт")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody final CreateNewProductDTO createNewProductDTO) {
        LOGGER.info("Запрос на создание нового продукта");
        return ResponseEntity.ok(productService.saveProduct(createNewProductDTO));
    }

    /**
     * Поиск продукта по названию.
     */
    @Operation(summary = "Поиск продукта по названию", description = "Возвращает список продуктов, соответствующих названию")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam(required = false) final String name) {
        LOGGER.info("Запрос на поиск продукта по имени: {}", name);
        return ResponseEntity.ok(productService.searchByName(name));
    }
}
