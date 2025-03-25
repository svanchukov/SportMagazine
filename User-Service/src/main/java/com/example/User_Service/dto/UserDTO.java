package com.example.User_Service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String name;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть валидным")
    private String email;

    @NotBlank(message = "Адрес не должен быть пустым")
    @Size(max = 200, message = "Адрес не должен превышать 200 символов")
    private String address;

    @NotBlank(message = "Телефон не должен быть пустым")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Телефон должен быть валидным (например, +1234567890)")
    private String phone;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<String> purchaseHistoryList;
}