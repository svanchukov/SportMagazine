package ru.svanchukov.user.User_Service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
public class UserDTO {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Имя должно иметь не меньше 3 символов и не больше 50 символов")
    private String name;

    @NotBlank
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    private String phoneNumber;

    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    private String jwtToken;
}
