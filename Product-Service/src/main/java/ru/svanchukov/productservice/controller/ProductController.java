package ru.svanchukov.productservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MessageSource messageSource;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // Загрузка продукта по ID перед каждым запросом
    @ModelAttribute("product")
    public ProductDTO product(@PathVariable("productId") Long productId) {
        logger.info("Запрос на загрузку продукта с ID: {}", productId);
        return productService.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("errors.product.not_found"));
    }

     @GetMapping
    public String getProduct(@PathVariable("productId") Long id, Model model) {
        logger.info("Запрос на получение продукта с ID: {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (authentication != null) ? authentication.getName() : "Unknown User";
        logger.info("Запрос от пользователя: {}", email);

        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));
        model.addAttribute("product", product);
        return "product/productDetails";
    }

    // Отображение формы для редактирования продукта
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
            return "product/edit";  // Возвращаем форму редактирования, если есть ошибки
        }

        productService.updateProduct(id, updateProductDTO);
        return "redirect:/products/{productId}"; // Перенаправление на страницу продукта по ID
    }

    // Удаление продукта
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
        return "product/error";
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
