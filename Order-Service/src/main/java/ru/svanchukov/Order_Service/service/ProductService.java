package ru.svanchukov.Order_Service.service;

import ru.svanchukov.Order_Service.dto.CreateNewProductDTO;
import ru.svanchukov.Order_Service.dto.ProductDTO;
import ru.svanchukov.Order_Service.entity.Product;
import ru.svanchukov.Order_Service.handler.ProductNotFoundException;
import ru.svanchukov.Order_Service.handler.ProductSavingException;
import ru.svanchukov.Order_Service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления продуктами в Order Service.
 * Предоставляет методы для создания, получения, обновления, удаления и поиска продуктов.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    /**
     * Сохраняет новый продукт в базе данных.
     */
    public ProductDTO saveProduct(final CreateNewProductDTO createNewProductDTO) {
        LOGGER.info("Создание нового продукта: {}", createNewProductDTO.getName());

        final Product product = new Product();
        product.setName(createNewProductDTO.getName());
        product.setCategory(createNewProductDTO.getCategory());
        product.setDescriptions(createNewProductDTO.getDescriptions());
        product.setPrice(createNewProductDTO.getPrice());
        product.setBrand(createNewProductDTO.getBrand());

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
     * Возвращает список всех продуктов.
     */
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Находит продукт по его идентификатору.
     */
    public Optional<ProductDTO> findById(final Long id) {
        return productRepository.findById(id)
                .map(this::mapToDto);
    }

    /**
     * Частично обновляет продукт по его идентификатору.
     */
    public void partialUpdate(final Long id, final Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));

        updates.forEach((key, value) -> {
            if (value == null) {
                return;
            }

            switch (key) {
                case "name" -> product.setName(value.toString());
                case "category" -> product.setCategory(value.toString());
                case "brand" -> product.setBrand(value.toString());
                case "descriptions" -> product.setDescriptions(value.toString());
                case "price" -> product.setPrice(Double.parseDouble(value.toString()));
                default -> LOGGER.warn("Неизвестное поле для обновления", key);
            }
        });

        productRepository.save(product);
    }

    /**
        Удаляет продукт по идентификатору.
     */
    public void delete(final Long productId) {
        LOGGER.info("Запрос на удаление продукта с ID: {}", productId);
        productRepository.findById(productId)
                .orElseThrow(() -> {
                    LOGGER.error("Продукт с ID {} не найден для удаления", productId);
                    return new ProductNotFoundException("Продукт с ID " + productId + " не найден");
                });

        productRepository.deleteById(productId);
        LOGGER.info("Продукт с ID: {} успешно удален", productId);
    }

    /**
     * Ищет продукты по имени.
     */
    public List<ProductDTO> searchByName(final String name) {
        LOGGER.info("Запрос на поиск продукта по имени: {}", name);
        if (name != null && !name.isEmpty()) {
            return productRepository.findByName(name)
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
        return findAll();
    }

    /**
     * Преобразует сущность Product в DTO.
     */
    private ProductDTO mapToDto(final Product product) {
        final ProductDTO dto = new ProductDTO();
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
