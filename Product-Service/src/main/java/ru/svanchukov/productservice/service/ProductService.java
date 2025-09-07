package ru.svanchukov.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    Сервис для работы с продуктом.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    /**
     * Создание и сохранение нового продукта.
     *
     * @param createNewProductDTO данные для нового продукта
     * @return сохранённый продукт в виде DTO
     */
    public ProductDTO saveProduct(CreateNewProductDTO createNewProductDTO) {
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
            throw new ProductSavingException("Ошибка при сохранении продукта: " + createNewProductDTO.getName());
        }

        return mapToDto(product);
    }

    /**
     * Получение всех продуктов.
     *
     * @param name (не используется в текущей реализации, но может быть применён для фильтрации)
     * @return список всех продуктов в виде DTO
     */
    public List<ProductDTO> findAll(String name) {
        LOGGER.info("Запрос на получение всех продуктов");
        List<Product> products = productRepository.findAll();
        LOGGER.info("Найдено {} продуктов", products.size());
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Поиск продукта по ID.
     *
     * @param id идентификатор продукта
     * @return продукт в виде Optional DTO
     */
    public Optional<ProductDTO> findById(Long id) {
        LOGGER.info("Поиск продукта с ID: {} в базе", id);
        Optional<ProductDTO> productDTO = productRepository.findById(id).map(this::mapToDto);
        productDTO.ifPresent(dto -> LOGGER.info("Найден продукт: {}", dto));
        return productDTO;
    }

    /**
     * Обновление данных продукта.
     *
     * @param id                идентификатор продукта
     * @param updateProductDTO  новые данные для обновления
     */
    public void updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        LOGGER.info("Запрос на обновление продукта с ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Продукт с ID {} не найден для обновления", id);
                    return new RuntimeException("Продукт с ID " + id + " не найден");
                });

        product.setName(updateProductDTO.getName());
        product.setCategory(updateProductDTO.getCategory());
        product.setBrand(updateProductDTO.getBrand());
        product.setDescriptions(updateProductDTO.getDescriptions());
        product.setPrice(updateProductDTO.getPrice());

        productRepository.save(product);
        LOGGER.info("Продукт с ID: {} успешно обновлен", id);
    }

    /**
     * Удаление продукта по ID.
     *
     * @param productId идентификатор продукта
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
     * Массовое увеличение цен на указанную сумму.
     *
     * @param amount сумма, на которую увеличивается цена каждого продукта
     */
    public void increasePrices(double amount) {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            product.setPrice(product.getPrice() + amount);
        }
        productRepository.saveAll(products);
        LOGGER.info("Цены всех продуктов увеличены на {}", amount);
    }

    /**
     * Преобразование сущности в DTO.
     *
     * @param product сущность продукта
     * @return DTO продукта
     */
    private ProductDTO mapToDto(Product product) {
        final ProductDTO dto = new ProductDTO();
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
