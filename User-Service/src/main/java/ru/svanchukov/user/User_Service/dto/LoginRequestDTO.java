package ru.svanchukov.user.User_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotNull
    private String password;

    @NotNull
    @Email
    private String email;
}
