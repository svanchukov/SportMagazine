package com.example.User_Service.service.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    // ConsumerFactory для UserCreatedEvent (для слушателя)
    @Bean
    public ConsumerFactory<String, UserCreatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "user-service-group");
        props.put("auto.offset.reset", "earliest");
        props.put("spring.json.trusted.packages", "com.example.User_Service.service.kafka");
        props.put("spring.json.value.default.type", "com.example.User_Service.service.kafka.UserCreatedEvent");

        JsonDeserializer<UserCreatedEvent> jsonDeserializer = new JsonDeserializer<>(UserCreatedEvent.class);
        ErrorHandlingDeserializer<UserCreatedEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    // KafkaListenerContainerFactory для UserCreatedEvent (для слушателя)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            logger.error("Ошибка десериализации записи: {}, исключение: {}", record, exception.getMessage());
        }));
        return factory;
    }

    // ProducerFactory для UserCreatedEvent (для отправки сообщений)
    @Bean
    public ProducerFactory<String, UserCreatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        props.put("spring.json.add.type.headers", true); // Для поддержки десериализации на стороне потребителя
        return new DefaultKafkaProducerFactory<>(props);
    }

    // KafkaTemplate для отправки UserCreatedEvent
    @Bean
    public KafkaTemplate<String, UserCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}