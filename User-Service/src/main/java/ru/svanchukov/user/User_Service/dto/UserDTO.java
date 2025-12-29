package ru.svanchukov.user.User_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigInteger;
import java.util.UUID;

/**
 * DTO пользователя.
 */
@Data
public class UserDTO  {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 50, message = "Имя должно иметь не меньше 3 символов и не больше 50 символов")
    private String name;

    @NotBlank
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    private String phoneNumber;

    @NotBlank(message = "Пароль должен быть обязательно")
    private String password;


}
