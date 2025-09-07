package ru.svanchukov.user.User_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Главный класс запуска User Service.
 * Включает автоконфигурацию Spring Boot и регистрацию сервиса в Eureka (или другом Discovery-сервисе).
 */
@SpringBootApplication
@EnableDiscoveryClient
@SuppressWarnings("checkstyle:FinalClass")
public class UserServiceApplication {

	/**
	 * Точка входа в приложение.
	 * @param args аргументы командной строки
	 */
	public static void main(final String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
