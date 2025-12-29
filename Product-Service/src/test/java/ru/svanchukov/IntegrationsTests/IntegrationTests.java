package ru.svanchukov.IntegrationsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.svanchukov.productservice.ProductServiceApplication;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.repository.ProductRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ContextConfiguration(classes = ProductServiceApplication.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Интеграционный тест: создание и проверка в БД")
    void createProduct_Integration() throws Exception {

        CreateNewProductDTO request = new CreateNewProductDTO();
        request.setName("Integration Phone");
        request.setBrand("Test Brand");
        request.setCategory("Test Category");
        request.setPrice(500.0);
        request.setDescriptions("Checking whole flow");

        mockMvc.perform(post("/product-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Phone"));
    }

    @Test
    @DisplayName("Интеграционный тест: обновление продукта")
    void updateProduct_Integration() throws Exception {

        Product product = new Product();
        product.setName("Old Name");
        product.setBrand("Brand");
        product.setDescriptions("Desc");
        product.setPrice(10.0);
        product.setCategory("Brandy");
        Product saved = productRepository.save(product);

        CreateNewProductDTO updateDto = new CreateNewProductDTO();
        updateDto.setName("New Name");
        updateDto.setBrand("Brand");
        updateDto.setDescriptions("Desc");
        updateDto.setCategory("Brandest");
        updateDto.setPrice(20.0);

        mockMvc.perform(put("/product-api/products/" + saved.getId() + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        Product updated = productRepository.findById(saved.getId()).orElseThrow();
        Assertions.assertThat(updated.getName()).isEqualTo("New Name");
        Assertions.assertThat(updated.getBrand()).isEqualTo("Brand");
    }

    @Test
    @DisplayName("Интеграционный тест: удаление продукта")
    void deleteProduct_Integration() throws Exception {

        Long productId = 4L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Iphone");
        product.setCategory("Phones");
        product.setBrand("Apple");
        product.setPrice(200.00);
        product.setDescriptions("Super Iphone");

        Product savedProduct = productRepository.save(product);

        mockMvc.perform(post("/product-api/products/" + savedProduct.getId() + "/delete"))
                .andExpect(status().isNoContent());

        boolean exists = productRepository.existsById(savedProduct.getId());
        Assertions.assertThat(exists).isFalse();
    }
}
































