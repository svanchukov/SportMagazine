package ru.svanchukov.email_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendUpdateProduct(String to, String productName, String description) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(to);
        mail.setFrom("vanchukov2018@yandex.ru");
        mail.setSubject("Обновление продукта: " + productName);
        mail.setText(description);

        mailSender.send(mail);
    }
}
