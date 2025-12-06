package ru.svanchukov.email_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.svanchukov.email_service.ProductUpdateMessage;
import ru.svanchukov.email_service.service.EmailService;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "Product-Updates", groupId = "email-group")
    public void listen(String message) {
        try {
            ProductUpdateMessage update = objectMapper.readValue(message, ProductUpdateMessage.class);
            emailService.sendUpdateProduct(
                    "vanchukov2018@yandex.ru",
                    update.getName(),
                    update.getMessage() + "Имя продукта: " + update.getName() + " Цена: " + update.getPrice()
            );
            System.out.println("Отправлено email сообщение с обновленным продуктом: " + update.getName());
        } catch (Exception e) {
            System.err.println("Ошибка при обработке Kafka-сообщения: " + e.getMessage());
        }
    }


}
