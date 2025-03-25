package ru.svanchukov.Order_Service.dto.OrderDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNewOrderDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}