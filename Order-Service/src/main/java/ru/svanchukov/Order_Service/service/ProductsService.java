package ru.svanchukov.Order_Service.service;

import ru.svanchukov.Order_Service.dto.CreateNewProductDTO;
import ru.svanchukov.Order_Service.dto.ProductDTO;
import ru.svanchukov.Order_Service.entity.Product;
import ru.svanchukov.Order_Service.handler.PriceException;
import ru.svanchukov.Order_Service.handler.ProductSavingException;
import ru.svanchukov.Order_Service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления продуктами в Order Service.
 * Предоставляет функциональность для создания продуктов и преобразования их в DTO.
 */
@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    /**
     * Сохраняет новый продукт в базе данных.
     */
    public ProductDTO saveProduct(CreateNewProductDTO createNewProductDTO) {
        LOGGER.info("Создание нового продукта: {}", createNewProductDTO.getName());

        Product product = new Product();
        product.setName(createNewProductDTO.getName());
        product.setCategory(createNewProductDTO.getCategory());
        product.setDescriptions(createNewProductDTO.getDescriptions());
        product.setPrice(createNewProductDTO.getPrice());
        product.setBrand(createNewProductDTO.getBrand());

        if (createNewProductDTO.getPrice() <= 0) {
            throw new PriceException("Цена должна быть больше 0");
        }

        try {
            productRepository.save(product);
            LOGGER.info("Продукт с именем {} успешно сохранен", createNewProductDTO.getName());
        } catch (Exception e) {
            LOGGER.error("Ошибка при сохранении продукта: {}", createNewProductDTO.getName(), e);
            throw new ProductSavingException("Ошибка при сохранении продукта");
        }

        return mapToDto(product);
    }

    /**
     * Преобразует сущность Product в DTO.
     */
    private ProductDTO mapToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId((long) product.getId());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setDescriptions(product.getDescriptions());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}