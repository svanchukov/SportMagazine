package ru.svanchukov.productservice.handler;

/**
 * Исключение, которое выбрасывается при ошибке
 * сохранения продукта в базе данных или другой системе хранения.
 */
public class ProductSavingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с заданным сообщением об ошибке.
     *
     * @param message текстовое описание причины ошибки сохранения продукта
     */
    public ProductSavingException(final String message) {
        super(message);
    }
}
