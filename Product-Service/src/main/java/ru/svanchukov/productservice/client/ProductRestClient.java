package ru.svanchukov.productservice.client;

import ru.svanchukov.productservice.dto.product.ProductDTO;

import java.util.List;
import java.util.Optional;

/**
 * REST client interface for managing products.
 */
public interface ProductRestClient {

    /**
     * Finds all products by name.
     */
    List<ProductDTO> findAllProduct(String name);

    /**
     * Creates a new product.
     */
    ProductDTO createProduct(String name, String category, String descriptions, Double price, String brand);

    /**
     * Finds product by ID.
     */
    Optional<ProductDTO> findProductById(int productId);

    /**
     * Updates an existing product.
     */
    void updateProduct(int productId, String name, String category, String descriptions, Double price, String brand);

    /**
     * Deletes a product by ID.
     */
    void deleteProduct(int productId);
}
