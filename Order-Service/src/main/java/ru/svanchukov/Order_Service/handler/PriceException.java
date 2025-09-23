package ru.svanchukov.Order_Service.handler;

/**
 * Исключение, выбрасываемое при отрицательной цене продукта.
 */
public class PriceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с указанным сообщением об ошибке.
     *
     * @param message текст ошибки
     */
    public PriceException(String message) {
        super(message);
    }
}
