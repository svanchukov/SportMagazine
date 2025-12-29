package ru.svanchukov.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
import ru.svanchukov.productservice.entity.Product;
import ru.svanchukov.productservice.handler.ProductNotFoundException;
import ru.svanchukov.productservice.handler.ProductSavingException;
import ru.svanchukov.productservice.repository.ProductRepository;
import ru.svanchukov.productservice.service.ProductsService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductsService productsService;

    @Test
    @DisplayName("saveProduct - успешное сохранение")
    void saveProduct_Success() {
        CreateNewProductDTO dto = new CreateNewProductDTO();
        dto.setName("Laptop");
        dto.setPrice(1500.0);

        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductDTO result = productsService.saveProduct(dto);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Laptop", result.getName());
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("saveProduct - ошибка базы")
    void saveProduct_Error() {
        when(productRepository.save(any())).thenThrow(new RuntimeException());

        Assertions.assertThrows(ProductSavingException.class,
                () -> productsService.saveProduct(new CreateNewProductDTO()));
    }

    @Test
    @DisplayName("findAll - получение всех продуктов")
    void findAll_Success() {
        Product p = new Product();
        p.setId(1L);
        when(productRepository.findAll()).thenReturn(List.of(p));

        List<ProductDTO> result = productsService.findAll();

        Assertions.assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("updateProduct - успешное обновление продукта")
    void updateProduct_Success() {
        Long id = 1L;
        Product existing = new Product();
        existing.setId(id);

        UpdateProductDTO updateDto = new UpdateProductDTO();
        updateDto.setName("New Name");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        productsService.updateProduct(id, updateDto);

        Assertions.assertEquals("New Name", existing.getName());
        verify(productRepository).save(existing);
    }

    @Test
    @DisplayName("delete - успешное удаление продукта")
    void delete_Success() {
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(true);

        productsService.delete(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete - не найден")
    void delete_NotFound() {
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(ProductNotFoundException.class, () -> productsService.delete(id));
    }

    @Test
    @DisplayName("searchByNameList - поиск продуктов по названию")
    void test_searchByNameList() {

        String name = "Iphone";
        Product p = new Product();
        p.setId(1L);
        p.setName(name);

        when(productRepository.findAllByName(name)).thenReturn(List.of(p));

        List<ProductDTO> result = productsService.searchByNameList(name);

        Assertions.assertFalse(result.isEmpty());

        ProductDTO foundProduct = result.get(0);

        System.out.println("Тест прошел успешно. Найден продукт: ID=" + foundProduct.getId() + ", Name=" + foundProduct.getName());

        Assertions.assertEquals(name, foundProduct.getName(),
                "Ожидали найти " + name + ", но получили " + foundProduct.getName());
    }

}






























