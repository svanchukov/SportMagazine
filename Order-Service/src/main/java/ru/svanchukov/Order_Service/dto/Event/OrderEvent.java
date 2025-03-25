package ru.svanchukov.Order_Service.dto.Event;

import lombok.Data;

@Data
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private String productName;
    private Double price;
}