package ru.svanchukov.Order_Service.handler;

/**
 * Исключение, выбрасываемое при ошибке сохранения продукта.
 */
public class ProductSavingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с указанным сообщением об ошибке.
     *
     * @param message текст ошибки
     */
    public ProductSavingException(String message) {
        super(message);
    }
}
