package ru.svanchukov.email_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailTesterService {

    private final EmailService emailService;

    @PostConstruct
    public void sendTo() {
        emailService.sendUpdateProduct(
                "vanchukov2018@yandex.ru",
                "Тестовое письмо",
                "Если ты видишь это письмо — SMTP работает!"
        );
    }
}
