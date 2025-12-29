package ru.svanchukov.IntegrationsTests.repositoryTests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import ru.svanchukov.productservice.ProductServiceApplication;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.repository.ProductRepository;

import java.util.List;

@DataJpaTest
@ContextConfiguration(classes = ProductServiceApplication.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Поиск продуктов по названию (регистронезависимо)")
    void findAllByName() {

        Product p1 = new Product();
        p1.setName("iPhone 15 Pro");
        p1.setBrand("Apple");
        p1.setCategory("Phone");
        p1.setDescriptions("High-end smartphone");
        p1.setPrice(1200.00);

        Product p2 = new Product();
        p2.setName("Samsung Galaxy");
        p2.setBrand("Samsung");
        p2.setCategory("Phone");
        p2.setDescriptions("Android flagship");
        p2.setPrice(1000.00);

        Product p3 = new Product();
        p3.setName("Apple Vision Iphone");
        p3.setBrand("Apple");
        p3.setCategory("Gadgets");
        p3.setDescriptions("Newest gadget");
        p3.setPrice(3500.00);

        productRepository.saveAll(List.of(p1, p2, p3));

        List<Product> result = productRepository.findAllByName("IPHONE");

        Assertions.assertThat(result).hasSize(2);

        Assertions.assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("iPhone 15 Pro", "Apple Vision Iphone")
                .doesNotContain("Samsung Galaxy");
    }

    @Test
    @DisplayName("Поиск возвращает пустой список, если совпадений нет")
    void findAllByName_ReturnsEmptyListWhenNoMatch() {

        Product p1 = new Product();
        p1.setName("iPhone 15 Pro");
        p1.setBrand("Apple");
        p1.setCategory("Phone");
        p1.setDescriptions("High-end smartphone");
        p1.setPrice(1200.00);
        productRepository.save(p1);


        List<Product> result = productRepository.findAllByName("Sony");

        Assertions.assertThat(result).isEmpty();
    }
}