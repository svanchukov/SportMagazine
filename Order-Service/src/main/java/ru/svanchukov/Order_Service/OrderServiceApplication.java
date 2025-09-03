package ru.svanchukov.Order_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Главный класс запуска сервиса Order Service.
 * Включает автоконфигурацию Spring Boot и регистрацию сервиса в Eureka или другом Discovery-сервисе.
 */
@SpringBootApplication
@EnableDiscoveryClient
public final class OrderServiceApplication {

	private OrderServiceApplication() {
		// Приватный конструктор предотвращает создание экземпляра класса
	}

	/**
	 * Точка входа в приложение.
	 */
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

}
