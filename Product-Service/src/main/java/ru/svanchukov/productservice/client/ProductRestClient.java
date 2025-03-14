package ru.svanchukov.productservice.client;

import ru.svanchukov.productservice.dto.product.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductRestClient {

    List<ProductDTO> findAllProduct(String name);

    ProductDTO createProduct(String name, String category, String descriptions, Double price, String brand);

    Optional<ProductDTO> findProductById(int productId);

    void updateProduct(int productId, String name, String category, String descriptions, Double price, String brand);

    void deleteProduct(int productId);
}
