package ru.svanchukov.Order_Service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductEventConsumer.class);

    @KafkaListener(topics = "product-events", groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        try {
            if (message.contains("не найден для удаления")) {
                throw new RuntimeException("Ошибка при обработке сообщения");
            }

            logger.info("Успешное сообщение: {}", message);
        } catch (Exception e) {
            logger.error("Ошибка обработки сообщения: {}", message);
            throw e;
        }
    }

    @KafkaListener(topics = "product-events-DLT", groupId = "order-group")
    public void listenDLT(String message) {
        logger.error("Получено сообщение в DLT: {}", message);
    }

}
