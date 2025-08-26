package ru.svanchukov.Order_Service.service;

import ru.svanchukov.Order_Service.dto.CreateNewProductDTO;
import ru.svanchukov.Order_Service.dto.ProductDTO;
import ru.svanchukov.Order_Service.entity.Product;
import ru.svanchukov.Order_Service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductDTO saveProduct(CreateNewProductDTO createNewProductDTO) {
        logger.info("Создание нового продукта: {}", createNewProductDTO.getName());

        Product product = new Product();
        product.setName(createNewProductDTO.getName());
        product.setCategory(createNewProductDTO.getCategory());
        product.setDescriptions(createNewProductDTO.getDescriptions());
        product.setPrice(createNewProductDTO.getPrice());
        product.setBrand(createNewProductDTO.getBrand());

        try {
            productRepository.save(product);
            logger.info("Продукт с именем {} успешно сохранен", createNewProductDTO.getName());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении продукта: {}", createNewProductDTO.getName(), e);
            throw new RuntimeException("Ошибка при сохранении продукта", e);
        }

        return mapToDto(product);
    }

    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDto);
    }

    public void partialUpdate(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Продукт с ID " + id + " не найден"));

        updates.forEach((key, value) -> {
            if (value == null) return;

            switch (key) {
                case "name" -> product.setName(value.toString());
                case "category" -> product.setCategory(value.toString());
                case "brand" -> product.setBrand(value.toString());
                case "descriptions" -> product.setDescriptions(value.toString());
                case "price" -> product.setPrice(Double.parseDouble(value.toString()));
            }
        });

        productRepository.save(product);
        // kafkaService.sendProductUpdateEvent(id, updates);
    }

    public void delete(Long productId) {
        logger.info("Запрос на удаление продукта с ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Продукт с ID {} не найден для удаления", productId);
                    return new RuntimeException("Продукт с ID " + productId + " не найден");
                });

        productRepository.deleteById(productId);
        logger.info("Продукт с ID: {} успешно удален", productId);
        // kafkaService.sendProductDeleteEvent(productId);
    }

    public List<ProductDTO> searchByName(String name) {
        logger.info("Запрос на поиск продукта по имени: {}", name);
        if (name != null && !name.isEmpty()) {
            return productRepository.findByName(name)
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
        return findAll();
    }

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
