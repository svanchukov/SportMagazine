package ru.svanchukov.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductsService;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "ProductsController", description = "Контроллер для управления списком продуктов")
public class ProductsController {

    private final ProductsService productsService;
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    @Operation(summary = "Получение всех продуктов", description = "Возвращает список всех продуктов")
    @GetMapping
    public String getProductsList(Model model,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "token", required = false) String token) {
        logger.info("Запрос на получение всех продуктов");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null && authentication.getPrincipal() instanceof UserDetails
                ? ((UserDetails) authentication.getPrincipal()).getUsername()
                : "Unknown User";
        logger.info("Запрос от пользователя: {}", email);

        List<ProductDTO> products = productsService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("name", name);
        model.addAttribute("token", token != null ? token : authentication.getCredentials());
        return "product/product";
    }

    @GetMapping("/new")
    public String getNewProductPage(Model model, @RequestParam(name = "token", required = false) String token) {
        logger.info("Показ формы создания нового продукта");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("createNewProductDTO", new CreateNewProductDTO());
        model.addAttribute("token", token != null ? token : authentication.getCredentials());
        return "product/new";
    }

    @Operation(summary = "Создание нового продукта", description = "Создаёт новый продукт и перенаправляет на список")
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("createNewProductDTO") CreateNewProductDTO createNewProductDTO,
                                BindingResult bindingResult,
                                Model model,
                                @RequestParam(name = "token", required = false) String token) {
        logger.info("Запрос на создание нового продукта");
        if (bindingResult.hasErrors()) {
            model.addAttribute("createNewProductDTO", createNewProductDTO);
            model.addAttribute("token", token);
            return "product/new";
        }
        ProductDTO productDTO = productsService.saveProduct(createNewProductDTO);
        model.addAttribute("product", productDTO);
        model.addAttribute("token", token);
        return "redirect:/products?token=" + token; // Добавляем token в редирект
    }

    @Operation(summary = "Поиск продукта по названию", description = "Возвращает список продуктов, соответствующих названию")
    @GetMapping("/search")
    public String searchByNameList(@RequestParam String name, Model model,
                                   @RequestParam(name = "token", required = false) String token) {
        logger.info("Поиск продукта по имени: {}", name);
        List<ProductDTO> products = productsService.searchByNameList(name.trim());
        if (!products.isEmpty()) {
            model.addAttribute("products", products);
            model.addAttribute("searchName", name);
        } else {
            model.addAttribute("error", "Продукты с таким именем не найдены");
        }
        model.addAttribute("token", token);
        return "product/productSpecificList";
    }
}