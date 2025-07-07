//package ru.svanchukov.productservice.kafka;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaLoggingListener {
//
//    @KafkaListener(topics = "logs", groupId = "logs-group")
//    public void listen(String logMessage) {
//        System.out.println("Received log message: " + logMessage);
//    }
//}
