package ru.svanchukov.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

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

    public List<ProductDTO> findAll(String name) {
        logger.info("Запрос на получение всех продуктов");
        List<Product> products = productRepository.findAll();
        logger.info("Найдено {} продуктов", products.size());
        return products.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Optional<ProductDTO> findById(Long id) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String cacheKey = "product:" + id;  // Ключ, по которому будет искать продукт

            // Пытаемся получить продукт из Redis
            String cachedProduct = jedis.get(cacheKey);

            if (cachedProduct != null) {
                ProductDTO productDTO = objectMapper.readValue(cachedProduct, ProductDTO.class);
                return Optional.of(productDTO);
            }

            Optional<ProductDTO> productDTO = productRepository.findById(id).map(this::mapToDto);
            if (productDTO.isPresent()) {
                try {
                    String jsonProduct = objectMapper.writeValueAsString(productDTO.get());
                    jedis.set(cacheKey, jsonProduct);  // Сохраняем продукт в Redis
                } catch (JsonProcessingException e) {
                    logger.error("Ошибка сериализации продукта для Redis", e);
                }
                return productDTO;
            }

            return Optional.empty();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        logger.info("Запрос на обновление продукта с ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Продукт с ID {} не найден для обновления", id);
                    return new RuntimeException("Продукт с ID " + id + " не найден");
                });

        // Обновляем основные данные продукта
        product.setName(updateProductDTO.getName());
        product.setCategory(updateProductDTO.getCategory());
        product.setBrand(updateProductDTO.getBrand());
        product.setPrice(updateProductDTO.getPrice());


        productRepository.save(product);
        logger.info("Продукт с ID: {} успешно обновлен", id);
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
    }

    public Optional<ProductDTO> searchByName(String name) {
        logger.info("Запрос на поиск продукта по имени: {}", name);
        if (name != null && !name.isEmpty()) {
            return productRepository.findByName(name)
                    .stream()
                    .findFirst()
                    .map(this::mapToDto);
        }
        return Optional.empty();
    }

    public void increasePrices(double amount) {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            product.setPrice(product.getPrice() + amount);
        }
        productRepository.saveAll(products);
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