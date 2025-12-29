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
import ru.svanchukov.productservice.controller.ProductsController;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.service.ProductsService;

import java.util.List;

// Убедись, что импорты ровно такие:
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // ПРАВИЛЬНЫЙ
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductsController.class)
@ContextConfiguration(classes = ProductServiceApplication.class)
public class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductsService productsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /product-api/products - получить весь список")
    void getProductsList_ReturnsList() throws Exception {

        ProductDTO p1 = new ProductDTO();
        p1.setName("Iphone");

        ProductDTO p2 = new ProductDTO();
        p2.setName("Samsung");

        when(productsService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/product-api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("POST /product-api/products - создание нового продукта")
    void createProduct_Success() throws Exception {
        CreateNewProductDTO requestDto = new CreateNewProductDTO();
        requestDto.setName("Nokia");
        requestDto.setCategory("Phones");
        requestDto.setPrice(120.00);
        requestDto.setBrand("Nokia");

        ProductDTO savedDto = new ProductDTO();
        savedDto.setId(10L);
        savedDto.setName("Nokia");
        savedDto.setBrand("Nokia");
        savedDto.setPrice(120.00);

        when(productsService.saveProduct(any(CreateNewProductDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/product-api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Nokia"));
    }

    @Test
    @DisplayName("GET /product-api/products/search - поиск по имени")
    void searchByName_Success() throws Exception{

        String searchName = "Iphone";
        ProductDTO product = new ProductDTO();
        product.setName("Iphone 15");

        when(productsService.searchByNameList(searchName)).thenReturn(List.of(product));

        mockMvc.perform(get("/product-api/products/search")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Iphone 15"));
    }
}































