package ru.svanchukov.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductService;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(("products/{productId:\\d+}"))
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "Контроллер для управления конкретным продуктом")
public class ProductController {

    private final ProductService productService;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Загрузка продукта по ID перед каждым запросом
    @ModelAttribute("product")
    public ProductDTO product(@PathVariable("productId") Long productId) {
        logger.info("Запрос на загрузку продукта с ID: {}", productId);
        return productService.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Продукт с ID " + productId + " не найден"));
    }

    // Получение продукта по ID (HTML)
    @Operation(summary = "Получение продукта по ID", description = "Возвращает страницу с данными продукта по ID")
    @GetMapping
    public String getProduct(@PathVariable("productId") Long id, Model model) {
        logger.info("Запрос на получение продукта с ID: {}", id);
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));
        model.addAttribute("product", product);
        return "product/productDetails";
    }

    // Отображение формы для редактирования продукта
    @Operation(summary = "Показать форму редактирования продукта", description = "Возвращает страницу для редактирования продукта")
    @GetMapping("edit")
    public String getProductEditPage(@PathVariable("productId") Long id, Model model) {
        logger.info("Показ формы редактирования продукта с ID: {}", id);
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));
        UpdateProductDTO updateProductDTO = mapToUpdateDto(product);
        model.addAttribute("updateProductDTO", updateProductDTO);
        return "product/edit";
    }

    // Обновление продукта
    @Operation(summary = "Обновление продукта по ID", description = "Обновляет продукт с определённым ID")
    @PostMapping("/edit")
    public String updateProduct(@PathVariable("productId") Long id,
                                @ModelAttribute("updateProductDTO") @Valid UpdateProductDTO updateProductDTO,
                                BindingResult bindingResult,
                                Model model) {
        logger.info("Запрос на обновление продукта с ID: {}", id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "product/edit"; // Если есть ошибки, возвращаем на страницу редактирования
        }
        productService.updateProduct(id, updateProductDTO);
        return "redirect:/products/{productId}"; // Перенаправляем на страницу с обновленным продуктом
    }



    // Удаление продукта
    @Operation(summary = "Удаление продукта по ID", description = "Удаляет продукт с определённым ID")
    @PostMapping("delete")
    public String deleteProduct(@PathVariable("productId") Long id) {
        logger.info("Запрос на удаление продукта с ID: {}", id);
        productService.delete(id);
        return "redirect:/products"; // Перенаправляем на список продуктов
    }

    // Обработчик ошибок (HTML)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));
        return "error"; // Возвращает страницу error.html
    }

    // Вспомогательный метод для маппинга ProductDTO в UpdateProductDTO
    private UpdateProductDTO mapToUpdateDto(ProductDTO product) {
        return new UpdateProductDTO(
                product.getName(),
                product.getCategory(),
                product.getDescriptions(),
                product.getPrice(),
                product.getBrand()
        );
    }
}
