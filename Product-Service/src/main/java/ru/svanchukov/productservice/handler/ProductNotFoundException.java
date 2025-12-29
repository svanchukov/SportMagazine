package ru.svanchukov.productservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, которое выбрасывается, когда продукт не найден
 * в системе.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с заданным сообщением об ошибке.
     *
     * @param message   текстовое описание причины отсутствия продукта
     * @param productId
     */
    public ProductNotFoundException(String message, Long productId) {
        super(message);
    }
}
