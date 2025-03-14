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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-api/products")
@RequiredArgsConstructor
@Tag(name = "ProductsController", description = "REST-контроллер для управления списком продуктов")
public class ProductsController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    @Operation(summary = "Получение всех продуктов", description = "Возвращает список всех продуктов")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProductsList() {
        logger.info("Запрос на получение всех продуктов");
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "Создание нового продукта", description = "Создаёт новый продукт")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateNewProductDTO createNewProductDTO) {
        logger.info("Запрос на создание нового продукта");
        return ResponseEntity.ok(productService.saveProduct(createNewProductDTO));
    }

    @Operation(summary = "Поиск продукта по названию", description = "Возвращает список продуктов, соответствующих названию")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam(required = false) String name) {
        logger.info("Запрос на поиск продукта по имени: {}", name);
        return ResponseEntity.ok(productService.searchByName(name));
    }
}