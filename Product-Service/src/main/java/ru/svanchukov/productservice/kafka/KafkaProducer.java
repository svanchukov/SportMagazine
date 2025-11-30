package ru.svanchukov.productservice.kafka;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.svanchukov.productservice.kafka.KafkaDTO.ProductUpdateEventDTO;

@Service
@AllArgsConstructor
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, ProductUpdateEventDTO> kafkaTemplate;

    private static final String TopicName = "product-updates";

    public void sendMessageToKafka(ProductUpdateEventDTO event) {
        LOGGER.info("Отправлено сообщение в Kafka: {} ", event);
        kafkaTemplate.send(TopicName, event);
    }
}
