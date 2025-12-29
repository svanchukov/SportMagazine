package ru.svanchukov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.svanchukov.productservice.ProductServiceApplication;
import ru.svanchukov.productservice.controller.ProductController;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.handler.ProductNotFoundException;
import ru.svanchukov.productservice.service.ProductService;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = ProductServiceApplication.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /product-api/products/{id}")
    void getProductById_Success() throws Exception {

        Long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Iphone");

        when(productService.findById(productId)).thenReturn(Optional.of(productDTO));

        mockMvc.perform(get("/product-api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Iphone"));
    }

    @Test
    @DisplayName("GET /product-api/products/{id} - вывод ошибки в консоль")
    void getProductById_NotSuccess() throws Exception {
        Long productId = 3L;
        String expectedMessage = "Такой продукт не найден";

        when(productService.findById(productId))
                .thenThrow(new ProductNotFoundException(expectedMessage, productId));

        mockMvc.perform(get("/product-api/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(expectedMessage));
    }

    @Test
    @DisplayName("PUT /product-api/products/{id}/edit - успешное изменение пользователя")
    void updateProductById_Success() throws Exception{
        Long productId = 5L;

        UpdateProductDTO updatedProduct = new UpdateProductDTO();
        updatedProduct.setName("Iphone");
        updatedProduct.setBrand("Apple");
        updatedProduct.setPrice(1200.00);
        updatedProduct.setCategory("Phones");

        ProductDTO result = new ProductDTO();
        result.setId(productId);
        result.setName("Iphone");
        result.setBrand("Apple");
        result.setPrice(1200.00);
        result.setCategory("Phones");

        when(productService.updateProduct(eq(productId), any(UpdateProductDTO.class)))
                .thenReturn(result);

        mockMvc.perform(put("/product-api/products/{id}/edit", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct))
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iphone"))
                .andExpect(jsonPath("$.brand").value("Apple"));
    }

    @Test
    @DisplayName("DELETE /product-api/products/{id}/delete - изменение продукта с ошибкой")
    void delete_product() throws Exception {
        Long productId = 4L;

        ProductDTO product = new ProductDTO();
        product.setId(productId);
        product.setName("Iphone");
        product.setBrand("Apple");
        product.setPrice(1200.00);
        product.setCategory("Phones");

        doNothing().when(productService).delete(productId);

        mockMvc.perform(post("/product-api/products/{id}/delete", productId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}


























