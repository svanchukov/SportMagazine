package ru.svanchukov.Order_Service.handler;

/**
 * Исключение, которое выбрасывается, когда продукт не найден
 * в системе.
 */
public class ProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с заданным сообщением об ошибке.
     *
     * @param message текстовое описание причины отсутствия продукта
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
