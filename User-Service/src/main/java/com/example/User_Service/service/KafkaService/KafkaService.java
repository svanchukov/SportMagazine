package com.example.User_Service.service.KafkaService;

import com.example.User_Service.dto.CreateNewUserDTO;
import com.example.User_Service.entity.User;
import com.example.User_Service.service.kafka.UserCreatedEvent;
import com.example.User_Service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void createUser(CreateNewUserDTO createNewUserDTO) {
        try {
            logger.info("Начало создания пользователя: {}", createNewUserDTO);

            User user = new User();
            user.setName(createNewUserDTO.getName());
            user.setPhone(createNewUserDTO.getPhone());
            user.setEmail(createNewUserDTO.getEmail());
            user.setAddress(createNewUserDTO.getAddress());

            // Сохраняем пользователя в базе данных
            userRepository.save(user);
            logger.info("Пользователь сохранен: {}", user);

            // Создаем событие о создании пользователя
            UserCreatedEvent event = new UserCreatedEvent(
                    null, // userId может быть null, если он генерируется в базе
                    createNewUserDTO.getName(),
                    createNewUserDTO.getEmail(),
                    createNewUserDTO.getAddress()
            );
            logger.info("Событие создано: {}", event);

            // Отправляем событие в Kafka
            logger.info("Отправка события в топик {}: {}", TOPIC, event);
            CompletableFuture<SendResult<String, UserCreatedEvent>> future = kafkaTemplate.send(TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Сообщение успешно отправлено в топик {}: {}", TOPIC, result);
                } else {
                    logger.error("Ошибка при отправке сообщения в топик {}: {}", TOPIC, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя или отправке в Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать пользователя или отправить событие в Kafka", e);
        }
    }
}