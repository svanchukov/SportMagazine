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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductService;

import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Контроллер для управления операциями над определённым продуктом.
 */
@Controller
@RequestMapping("products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final MessageSource messageSource;

    /**
     * Загружает продукт по ID перед каждым запросом и добавляет его в модель под атрибутом.
     */
    @ModelAttribute("product")
    public ProductDTO product(@PathVariable("productId") Long productId) {
        LOGGER.info("Запрос на загрузку продукта с ID: {}", productId);
        return productService.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("errors.product.not_found"));
    }

    /**
     * Получение страницы с подробной информацией о продукте.
     */
    @GetMapping
    public String getProduct(@PathVariable("productId") Long id, Model model) {
        LOGGER.info("Запрос на получение продукта с ID: {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (authentication != null) ? authentication.getName() : "Unknown User";
        LOGGER.info("Запрос от пользователя: {}", email);

        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));
        model.addAttribute("product", product);
        return "product/productDetails";
    }

    /**
     * Показ формы редактирования продукта.
     *
     * @param id    идентификатор продукта
     * @param model модель для передачи данных в представление
     * @return имя HTML-шаблона страницы редактирования
     */
    @GetMapping("edit")
    public String getProductEditPage(@PathVariable("productId") Long id, Model model) {
        LOGGER.info("Показ формы редактирования продукта с ID: {}", id);
        ProductDTO product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));
        UpdateProductDTO updateProductDTO = mapToUpdateDto(product);
        model.addAttribute("updateProductDTO", updateProductDTO);
        return "product/edit";
    }

    /**
     * Обновление информации о продукте.
     *
     * @param id               идентификатор продукта
     * @param updateProductDTO данные для обновления
     * @param bindingResult    результат валидации
     * @param model            модель для передачи ошибок (если есть)
     * @return редирект на страницу продукта или форма редактирования при ошибках
     */
    @PostMapping("/edit")
    public String updateProduct(final @PathVariable("productId") Long id,
                                final @ModelAttribute("updateProductDTO") @Valid UpdateProductDTO updateProductDTO,
                                final BindingResult bindingResult,
                                final Model model) {
        LOGGER.info("Запрос на обновление продукта с ID: {}", id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return "product/edit";  // Возврат формы редактирования, если есть ошибки
        }

        productService.updateProduct(id, updateProductDTO);
        return "redirect:/products/{productId}";
    }

    /**
     * Удаление продукта по ID.
     *
     * @param id идентификатор продукта
     * @return редирект на список продуктов
     */
    @PostMapping("delete")
    public String deleteProduct(@PathVariable("productId") Long id) {
        LOGGER.info("Запрос на удаление продукта с ID: {}", id);
        productService.delete(id);
        return "redirect:/products";
    }

    /**
     * Обработка исключения при отсутствии продукта в базе.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception,
                                               Model model,
                                               HttpServletResponse response,
                                               Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0],
                        exception.getMessage(), locale));
        return "product/error";
    }

    /**
     * Преобразует {@link ProductDTO} в {@link UpdateProductDTO} для редактирования.
     *
     * @param product DTO продукта
     * @return DTO для обновления
     */
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
