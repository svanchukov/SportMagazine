//package ru.svanchukov.productservice.kafka;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaLoggingProducer {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public KafkaLoggingProducer(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendLogToKafka(String logMessage) {
//        kafkaTemplate.send("logs", logMessage);
//    }
//}
