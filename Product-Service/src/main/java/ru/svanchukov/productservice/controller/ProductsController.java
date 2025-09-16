package ru.svanchukov.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductsService;

import java.util.List;

/**
 * Контроллер для управления общим списком продуктов.
 */
@RestController
@RequestMapping("/product-api/products")
@RequiredArgsConstructor
@Tag(name = "Products List", description = "API для работы со списком продуктов")
public class ProductsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    private final ProductsService productsService;

    /**
     * Отображает список всех продуктов.
     */
    @GetMapping
    @Operation(summary = "Получить список продуктов", description = "Возвращает список всех доступных продуктов")
    public ResponseEntity<List<ProductDTO>> getProductsList() {
        LOGGER.info("Запрос на получение всех продуктов");

        List<ProductDTO> listProducts = productsService.findAll();
        return ResponseEntity.ok(listProducts);
    }

    /**
     * Создание нового продукта.
     */
    @PostMapping
    @Operation(summary = "Создать новый продукт", description = "Добавляет новый продукт в базу данных")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateNewProductDTO createNewProductDTO) {
        LOGGER.info("Запрос на создание нового продукта");

        ProductDTO savedProduct = productsService.saveProduct(createNewProductDTO);

        return ResponseEntity.status(201).body(savedProduct);
    }

    /**
     * Поиск продукта по названию.
     */
    @GetMapping("/search")
    @Operation(summary = "Поиск продуктов по имени", description = "Возвращает список продуктов, название которых содержит переданный параметр")
    public ResponseEntity<List<ProductDTO>> searchByNameList(
            @Parameter(description = "Название продукта для поиска", example = "iPhone")
            @RequestParam(name = "name", required = false) String name) {
        LOGGER.info("Поиск продукта по имени: {}", name);
        List<ProductDTO> result = (name == null || name.isBlank())
                ? productsService.findAll()
                : productsService.searchByNameList(name);
        return ResponseEntity.ok(result);
    }
}
