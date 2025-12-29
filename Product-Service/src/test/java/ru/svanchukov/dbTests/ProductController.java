package ru.svanchukov.dbTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.svanchukov.productservice.ProductServiceApplication;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.repository.ProductRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = ProductServiceApplication.class)
public class ProductController extends DbTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Testcontainers: удаление продукта")
    void deleteProduct_Integration() throws Exception {

        Product product = new Product();
        product.setName("Iphone");
        product.setCategory("Phones");
        product.setBrand("Apple");
        product.setPrice(200.00);

        Product saved = productRepository.save(product);

        mockMvc.perform(
                post("/product-api/products/" + saved.getId() + "/delete")
        ).andExpect(status().isNoContent());

        Assertions.assertThat(
                productRepository.existsById(saved.getId())
        ).isFalse();
    }
}
