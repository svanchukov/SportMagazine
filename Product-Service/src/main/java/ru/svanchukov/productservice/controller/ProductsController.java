package ru.svanchukov.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "ProductsController", description = "Контроллер для управления списком продуктов")
public class ProductsController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    // Получение списка всех продуктов (HTML)
    @Operation(summary = "Получение всех продуктов", description = "Возвращает список всех продуктов")
    @GetMapping
    public String getProductsList(Model model, @RequestParam(name = "name", required = false) String name) {
        logger.info("Запрос на получение всех продуктов");
        List<ProductDTO> products = productService.findAll(name);
        model.addAttribute("products", products);
        model.addAttribute("name", name);
        return "product/product";
    }

    @GetMapping("/new")
    public String getNewProductPage(Model model) {
        logger.info("Показ формы создания нового продукта");
        model.addAttribute("createNewProductDTO", new CreateNewProductDTO());
        return "product/new";
    }

    // Создание нового продукта (HTML)
    @Operation(summary = "Создание нового продукта", description = "Создаёт новый продукт и перенаправляет на список")
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("createNewProductDTO") CreateNewProductDTO createNewProductDTO,
                                BindingResult bindingResult,
                                Model model) {
        logger.info("Запрос на создание нового продукта");
        if (bindingResult.hasErrors()) {
            model.addAttribute("createNewProductDTO", createNewProductDTO);
            return "product/new";
        }
        ProductDTO productDTO = productService.saveProduct(createNewProductDTO);
        model.addAttribute("product", productDTO);
        return "redirect:/products";
    }

    // Поиск продукта по названию (HTML)
    @Operation(summary = "Поиск продукта по названию", description = "Возвращает список продуктов, соответствующих названию")
    @GetMapping("/search")
    public String searchByName(@RequestParam(required = false) String name, Model model) {
        logger.info("Запрос на поиск продукта по имени: {}", name);
        Optional<ProductDTO> product = productService.searchByName(name);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product/product_single";
        } else {
            model.addAttribute("error", "Продукт с таким именем не найден");
            return "redirect:/product";
        }
    }
}