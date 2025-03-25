package com.example.User_Service.service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        logger.info("Отправка события в топик user-events: {}", event);
        kafkaTemplate.send("user-events", event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("Событие успешно отправлено: {}", result);
                    } else {
                        logger.error("Ошибка при отправке события: {}", ex.getMessage(), ex);
                    }
                });
    }

    @KafkaListener(topics = "user-events", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(UserCreatedEvent event) {
        logger.info("Получено событие из user-events: {}", event);
    }
}