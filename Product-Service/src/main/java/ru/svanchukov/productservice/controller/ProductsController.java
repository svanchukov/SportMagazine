package ru.svanchukov.productservice.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductsService;

import java.util.List;

/**
 * Контроллер для управления общим списком продуктов.
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);
    private static final String TOKEN_ATTRIBUTE = "token";

    private final ProductsService productsService;

    /**
     * Отображает список всех продуктов.
     *
     * @param model модель для передачи данных в представление
     * @param name  имя продукта для отображения (опционально)
     * @param token JWT токен (опционально)
     * @return имя представления product/product
     */
    @GetMapping
    public String getProductsList(Model model,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = TOKEN_ATTRIBUTE, required = false) String token) {
        LOGGER.info("Запрос на получение всех продуктов");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null && authentication.getPrincipal() instanceof UserDetails
                ? ((UserDetails) authentication.getPrincipal()).getUsername()
                : "Unknown User";
        LOGGER.info("Запрос от пользователя: {}", email);

        List<ProductDTO> products = productsService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("name", name);
        model.addAttribute(TOKEN_ATTRIBUTE, token != null ? token : authentication.getCredentials());
        return "product/product";
    }

    /**
     * Отображает страницу создания нового продукта.
     */
    @GetMapping("/new")
    public String getNewProductPage(Model model, @RequestParam(name = TOKEN_ATTRIBUTE, required = false) String token) {
        LOGGER.info("Показ формы создания нового продукта");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("createNewProductDTO", new CreateNewProductDTO());
        model.addAttribute(TOKEN_ATTRIBUTE, token != null ? token : authentication.getCredentials());
        return "product/new";
    }

    /**
        Создание нового продукта.
     */
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("createNewProductDTO") CreateNewProductDTO createNewProductDTO,
                                BindingResult bindingResult,
                                Model model,
                                @RequestParam(name = TOKEN_ATTRIBUTE, required = false) String token) {
        LOGGER.info("Запрос на создание нового продукта");
        if (bindingResult.hasErrors()) {
            model.addAttribute("createNewProductDTO", createNewProductDTO);
            model.addAttribute(TOKEN_ATTRIBUTE, token);
            return "product/new";
        }
        ProductDTO productDTO = productsService.saveProduct(createNewProductDTO);
        model.addAttribute("product", productDTO);
        model.addAttribute(TOKEN_ATTRIBUTE, token);
        return "redirect:/products?token=" + token; // Добавляем token в редирект
    }

    /**
        Поиск продукта по названию.
     */
    @GetMapping("/search")
    public String searchByNameList(@RequestParam String name, Model model,
                                   @RequestParam(name = TOKEN_ATTRIBUTE, required = false) String token) {
        LOGGER.info("Поиск продукта по имени: {}", name);
        List<ProductDTO> products = productsService.searchByNameList(name.trim());
        if (!products.isEmpty()) {
            model.addAttribute("products", products);
            model.addAttribute("searchName", name);
        } else {
            model.addAttribute("error", "Продукты с таким именем не найдены");
        }
        model.addAttribute(TOKEN_ATTRIBUTE, token);
        return "product/productSpecificList";
    }
}