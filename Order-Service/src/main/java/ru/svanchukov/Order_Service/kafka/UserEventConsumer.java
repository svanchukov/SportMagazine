package ru.svanchukov.Order_Service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    @KafkaListener(topics = "user-events", groupId = "order-service-group")
    public void consume(UserCreatedEvent event) {
        System.out.println("Получено событие о создании пользователя: " + event);
    }
}