package ru.svanchukov.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.handler.ProductNotFoundException;
import ru.svanchukov.productservice.handler.ProductSavingException;
import ru.svanchukov.productservice.kafka.KafkaProducer;
import ru.svanchukov.productservice.repository.ProductRepository;
import ru.svanchukov.productservice.service.ProductService;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    @DisplayName("saveProduct - успешное сохранение пользователя")
    void test_saveProduct_Success() {

        CreateNewProductDTO createNewProductDTO = new CreateNewProductDTO();
        createNewProductDTO.setName("Iphone");
        createNewProductDTO.setBrand("Apple");
        createNewProductDTO.setCategory("Electronics");
        createNewProductDTO.setDescriptions("Smartphone");
        createNewProductDTO.setPrice(999.99);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ProductDTO result = productService.saveProduct(createNewProductDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Iphone", result.getName());
        Assertions.assertEquals("Apple", result.getBrand());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("saveProduct - неудачное сохранение пользователя")
    void test_saveProduct_NotSuccess() {

        CreateNewProductDTO createNewProductDTO = new CreateNewProductDTO();
        createNewProductDTO.setName("Iphone");
        createNewProductDTO.setBrand("Apple");
        createNewProductDTO.setCategory("Electronics");
        createNewProductDTO.setDescriptions("Smartphone");
        createNewProductDTO.setPrice(999.99);

        doThrow(new ProductSavingException("DB error"))
                .when(productRepository).save(any(Product.class));

        // Проверяем, что ProductSavingException выбрасывается
        ProductSavingException exception = Assertions.assertThrows(
                ProductSavingException.class,
                () -> productService.saveProduct(createNewProductDTO)
        );
    }

    @Test
    @DisplayName("findAll - нахождение всех продуктов")
    void test_findAll_Success() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Iphone");
        product1.setBrand("Apple");
        product1.setCategory("Electronics");
        product1.setDescriptions("Smartphone");
        product1.setPrice(999.99);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Samsung");
        product2.setBrand("Samsung");
        product2.setCategory("Electronics");
        product2.setDescriptions("Smartphone");
        product2.setPrice(699.99);

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductDTO> result = productService.findAll(null);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Iphone", result.get(0).getName());
        Assertions.assertEquals("Samsung", result.get(1).getName());
    }

    @Test
    @DisplayName("findById - нахождение продукта по id")
    void test_findById_Success() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Iphone");
        product.setBrand("Apple");
        product.setPrice(999.99);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Optional<ProductDTO> result = productService.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Iphone", result.get().getName());
        Assertions.assertEquals("Apple", result.get().getBrand());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - продукта нет")
    void test_findById_NotFound() {
        when(productRepository.findById(50L))
                .thenReturn(Optional.empty());

        Optional<ProductDTO> result = productService.findById(50L);

        Assertions.assertTrue(result.isEmpty());

        verify(productRepository, times(1)).findById(50L);
    }

    @Test
    @DisplayName("updateProduct - успешное изменение продукта")
    void test_updateProduct_Success() {

        Long productId = 1L;
        Product product = new Product();
        product.setId(1L);
        product.setName("Old Name");
        product.setPrice(100.0);

        UpdateProductDTO updateProductDTO = new UpdateProductDTO();
        updateProductDTO.setName("New Name");
        updateProductDTO.setPrice(150.0);
        updateProductDTO.setBrand("New Brand");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductDTO result = productService.updateProduct(productId, updateProductDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("New Name", result.getName());
        Assertions.assertEquals(150.0, result.getPrice());
        Assertions.assertEquals("New Brand", result.getBrand());

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(kafkaProducer, times(1)).sendMessageToKafka(any());
    }

    @Test
    @DisplayName("updatedProduct - неуспешное изменение продукта")
    void test_updatedProduct_NotSuccess() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        UpdateProductDTO productDTO = new UpdateProductDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenThrow(new ProductSavingException("DB isn't saving"));

        Assertions.assertThrows(ProductSavingException.class,
                () -> productService.updateProduct(productId, productDTO));

        verify(productRepository, never()).save(any());
        verify(kafkaProducer, never()).sendMessageToKafka(any());
    }

    @Test
    @DisplayName("delete - успешное удаление пользователя")
    void test_deleteProduct_Success() {

        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        productService.delete(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("delete - неудачное удаление пользователя")
    void test_deleteProduct_NotSuccess() {

        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(false);

        Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.delete(productId));

        verify(productRepository, never()).deleteById(productId);
    }

    @Test
    @DisplayName("increasePrices - повышение цен")
    void test_increasePrices() {

        Product p1 = new Product();
        p1.setId(1L);
        p1.setPrice(100.0);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setPrice(200.0);

        when(productRepository.findAll()).thenReturn(List.of(p1,p2));
        double amount = 50.0;

        productService.increasePrices(amount);

        Assertions.assertEquals(150.0, p1.getPrice());
        Assertions.assertEquals(250.0, p2.getPrice());

        verify(productRepository, times(1)).saveAll(anyList());
    }
}

































