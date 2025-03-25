package ru.svanchukov.Order_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.svanchukov.Order_Service.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}