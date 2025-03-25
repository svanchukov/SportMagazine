package com.example.User_Service.service.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreatedEvent {

    private String userId;
    private String username;
    private String email;
    private String address;

    @JsonCreator
    public UserCreatedEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("username") String username,
            @JsonProperty("email") @NotBlank(message = "Email не должен быть пустым") @Email(message = "Email должен быть валидным") String email,
            @JsonProperty("address") @NotBlank(message = "Адрес не должен быть пустым") @Size(max = 200, message = "Адрес не должен превышать 200 символов") String address) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.address = address;
    }

}