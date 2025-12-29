package ru.svanchukov.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.handler.ProductNotFoundException;
import ru.svanchukov.productservice.service.ProductService;

import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product-api/products/{productId:\\d+}")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API для работы с продуктами")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final MessageSource messageSource;

    @Operation(summary = "Загрузить продукт", description = "Загружает продукт по ID перед выполнением других запросов")
    public ResponseEntity<ProductDTO> product(@PathVariable("productId") Long productId) {
        LOGGER.info("Запрос на загрузку продукта с ID: {}", productId);
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException("Product with id: %d not found", productId));
    }

    @GetMapping
    @Operation(summary = "Получить продукт по ID", description = "Возвращает детальную информацию о продукте")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") Long productId) {
        LOGGER.info("Запрос на получение продукта с ID: {}", productId);
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException("Продукт с ID " + productId + " не найден", productId));
    }

    @PutMapping("/edit")
    @Operation(summary = "Обновить продукт", description = "Обновляет информацию о продукте по ID")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") Long id,
            @RequestBody @Valid UpdateProductDTO updateProductDTO,
            final BindingResult bindingResult) {
        LOGGER.info("Запрос на обновление продукта с ID: {}", id);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (oldvalue, newvalue) -> oldvalue
                    ));

            return ResponseEntity.badRequest().body(errors);
        }

        ProductDTO updatedProductDTO = productService.updateProduct(id, updateProductDTO);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @PostMapping("delete")
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт по ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long id) {
        LOGGER.info("Запрос на удаление продукта с ID: {}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @Operation(hidden = true) // скрываем из Swagger
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception,
                                                                      Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        Objects.requireNonNull(this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale))));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFound(ProductNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage()
                ));
    }



}
