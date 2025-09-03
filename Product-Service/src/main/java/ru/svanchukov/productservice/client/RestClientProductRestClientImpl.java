package ru.svanchukov.productservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.svanchukov.productservice.dto.product.CreateNewProductDTO;
import ru.svanchukov.productservice.dto.product.ProductDTO;
import ru.svanchukov.productservice.dto.product.UpdateProductDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Реализация {ProductRestClient} с использованием {RestClient}.
 */
@RequiredArgsConstructor
public class RestClientProductRestClientImpl implements ProductRestClient {

    private static final ParameterizedTypeReference<List<ProductDTO>> PRODUCT_TYPE_REFERENCE =
            new ParameterizedTypeReference<List<ProductDTO>>() { };

    private final RestClient restClient;

    /**
     * Возвращает список продуктов с фильтрацией по имени.
     */
    @Override
    public List<ProductDTO> findAllProduct(String name) {
        return this.restClient
                .get()
                .uri("order-api/products?name={name}", name)
                .retrieve()
                .body(PRODUCT_TYPE_REFERENCE);
    }

    /**
     * Создает новый продукт.
     */
    @Override
    public ProductDTO createProduct(final String name, final String category, final String descriptions, final Double price, final String brand) {
        final CreateNewProductDTO newProductDTO = new CreateNewProductDTO();
        newProductDTO.setName(name);
        newProductDTO.setCategory(category);
        newProductDTO.setDescriptions(descriptions);
        newProductDTO.setPrice(price);
        newProductDTO.setBrand(brand);
        return this.restClient
                .post()
                .uri("order-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newProductDTO)
                .retrieve()
                .body(ProductDTO.class);
    }

    /**
     * Ищет продукт по ID.
     */
    @Override
    public Optional<ProductDTO> findProductById(int productId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/order-api/products/{productId}", productId)
                    .retrieve()
                    .body(ProductDTO.class));
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        }
    }

    /**
     * Обновляет продукт.
     */
    @Override
    public void updateProduct(int productId, String name, String category, String descriptions, Double price, String brand) {
        try {
            this.restClient.patch()
                    .uri("order-api/products/{productId}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateProductDTO(name, category, descriptions, price, brand))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            final ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException("Bad request when updating product", exception,
                    (List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    /**
     * Удаляет продукт по ID.
     */
    @Override
    public void deleteProduct(int productId) {
        try {
            this.restClient.delete()
                    .uri("order-api/products/{productId}", productId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
