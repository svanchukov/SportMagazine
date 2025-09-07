package ru.svanchukov.productservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.handler.ProductNotFoundException;
import ru.svanchukov.productservice.handler.ProductSavingException;
import ru.svanchukov.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
    Сервис для работы с продуктами.
 */
@Service
@RequiredArgsConstructor
public class ProductsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    /**
     * Создание и сохранение нового продукта.
     */
    public ProductDTO saveProduct(final CreateNewProductDTO createNewProductDTO) {
        LOGGER.info("Создание нового продукта: {}", createNewProductDTO.getName());

        Product product = new Product();
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
     * Получение всех продуктов из базы.
     */
    public List<ProductDTO> findAll() {
        LOGGER.info("Запрос на получение всех продуктов");
        List<Product> products = productRepository.findAll();
        LOGGER.info("Найдено {} продуктов", products.size());
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Поиск продукта по его ID.
     */
    public Optional<ProductDTO> findById(Long id) {
        LOGGER.info("Данные получены из базы для ID: {}", id);
        return productRepository.findById(id).map(this::mapToDto);
    }

    /**
     * Обновление данных существующего продукта.
     */
    public void updateProduct(Long id, final UpdateProductDTO updateProductDTO) {
        LOGGER.info("Запрос на обновление продукта с ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Продукт с ID {} не найден для обновления", id);
                    return new RuntimeException("Продукт с ID " + id + " не найден");
                });

        product.setName(updateProductDTO.getName());
        product.setCategory(updateProductDTO.getCategory());
        product.setBrand(updateProductDTO.getBrand());
        product.setPrice(updateProductDTO.getPrice());

        productRepository.save(product);
        LOGGER.info("Продукт с ID: {} успешно обновлен", id);
    }

    /**
     * Удаление продукта по ID.
     */
    public void delete(Long productId) {
        LOGGER.info("Запрос на удаление продукта с ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            LOGGER.error("Продукт с ID {} не найден для удаления", productId);
            throw new ProductNotFoundException("Продукт с ID " + productId + " не найден");
        }

        productRepository.deleteById(productId);
        LOGGER.info("Продукт с ID: {} успешно удален", productId);
    }

    /**
     * Поиск продуктов по имени (или список всех подходящих).
     */
    public List<ProductDTO> searchByNameList(String name) {
        return productRepository.findAllByName(name)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразование сущности Product в DTO.
     */
    private ProductDTO mapToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId((long) product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setDescriptions(product.getDescriptions());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
