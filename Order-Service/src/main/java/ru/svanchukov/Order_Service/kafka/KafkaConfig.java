package ru.svanchukov.Order_Service.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    // KafkaTemplate для отправки строковых сообщений (для ProductEventConsumer)
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new DefaultKafkaProducerFactory<>(props);
    }

    // ConsumerFactory для String (для ProductEventConsumer)
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-group");
        props.put("auto.offset.reset", "earliest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    // ConsumerFactory для UserCreatedEvent (для обработки сообщений из user-events)
    @Bean
    public ConsumerFactory<String, com.example.User_Service.service.kafka.UserCreatedEvent> userConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-group"); // Используем ту же группу, что и для product-events
        props.put("auto.offset.reset", "earliest");
        props.put("spring.json.trusted.packages", "com.example.User_Service.service.kafka");
        props.put("spring.json.value.default.type", "com.example.User_Service.service.kafka.UserCreatedEvent");

        JsonDeserializer<com.example.User_Service.service.kafka.UserCreatedEvent> jsonDeserializer =
                new JsonDeserializer<>(com.example.User_Service.service.kafka.UserCreatedEvent.class);
        ErrorHandlingDeserializer<com.example.User_Service.service.kafka.UserCreatedEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    // KafkaListenerContainerFactory для String (для ProductEventConsumer)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    // KafkaListenerContainerFactory для UserCreatedEvent (для обработки user-events)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, com.example.User_Service.service.kafka.UserCreatedEvent> userKafkaListenerContainerFactory(
            ConsumerFactory<String, com.example.User_Service.service.kafka.UserCreatedEvent> userConsumerFactory,
            DefaultErrorHandler userErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, com.example.User_Service.service.kafka.UserCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory);
        factory.setCommonErrorHandler(userErrorHandler);
        return factory;
    }

    // ErrorHandler для ProductEventConsumer
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> {
                    logger.info("Отправка сообщения в DLT: топик={}, ключ={}, сообщение={}",
                            record.topic() + "-DLT", record.key(), record.value());
                    return new TopicPartition(record.topic() + "-DLT", record.partition());
                });

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    }

    // ErrorHandler для UserEventConsumer
    @Bean
    public DefaultErrorHandler userErrorHandler(KafkaTemplate<String, com.example.User_Service.service.kafka.UserCreatedEvent> userKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(userKafkaTemplate,
                (record, exception) -> {
                    logger.info("Отправка сообщения в DLT: топик={}, ключ={}, сообщение={}",
                            record.topic() + "-DLT", record.key(), record.value());
                    return new TopicPartition(record.topic() + "-DLT", record.partition());
                });

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    }

    // KafkaTemplate для UserCreatedEvent (для отправки в DLT)
    @Bean
    public KafkaTemplate<String, com.example.User_Service.service.kafka.UserCreatedEvent> userKafkaTemplate() {
        return new KafkaTemplate<>(userProducerFactory());
    }

    // ProducerFactory для UserCreatedEvent (для отправки в DLT)
    private ProducerFactory<String, com.example.User_Service.service.kafka.UserCreatedEvent> userProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        props.put("spring.json.add.type.headers", true);
        return new DefaultKafkaProducerFactory<>(props);
    }
}