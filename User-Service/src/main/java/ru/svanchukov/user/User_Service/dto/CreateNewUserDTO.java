package ru.svanchukov.user.User_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateNewUserDTO {
    
    @NotBlank
    @Size(min = 3, max = 50, message = "Имя должно иметь не меньше 3 символов и не больше 50 символов")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^(\\+7[0-9]{9,10}|8[0-9]{9,10})$", message = "Номер телефона должен начинаться с 8 или +7 и содержать 10 или 11 цифр")
    private String phoneNumber;

    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    private String jwtToken;
}
