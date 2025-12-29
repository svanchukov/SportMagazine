package ru.svanchukov.user.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.svanchukov.user.User_Service.UserServiceApplication;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = UserServiceApplication.class)
@Transactional
public class UserIntegrationsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Интеграционный тест: создание пользователя")
    void createUser_Integration() throws Exception {

        CreateNewUserDTO request = new CreateNewUserDTO();
        request.setName("Alex");
        request.setEmail("alex@test.com");
        request.setPassword("12345");
        request.setPhoneNumber("+79687899067");

        mockMvc.perform(post("/users-api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alex@test.com"));

        Assertions.assertThat(userRepository.findByEmail("alex@test.com")).isPresent();
    }

    @Test
    @DisplayName("Интеграционный тест: обновление пользователя")
    void updateUser_Integration() throws Exception {

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@test.com");
        user.setPhoneNumber("+79687906859");
        user.setPassword("12345");

        User savedUser = userRepository.save(user);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName("New name");
        updateUserDTO.setEmail("new@yandex.ru");
        updateUserDTO.setPhoneNumber("+79000999089");
        updateUserDTO.setPassword("12345");

        mockMvc.perform(patch("/users-api/users/{id}/edit", savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New name"))
                .andExpect(jsonPath("$.email").value("new@yandex.ru"));

        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();

        Assertions.assertThat(updatedUser.getName()).isEqualTo("New name");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("new@yandex.ru");
    }

    @Test
    @DisplayName("Интеграционный тест: удаление пользователя")
    void deleteUser_Integration() throws Exception {

        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@test.com");
        user.setPassword("12345");
        user.setPhoneNumber("+79039879078");

        User saved = userRepository.save(user);

        mockMvc.perform(post("/users-api/users/" + saved.getId() + "/delete"))
                .andExpect(status().isNoContent());

        Assertions.assertThat(userRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("Интеграционный тест: получение пользователя по ID")
    void getUser_Integration() throws Exception {

        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@test.com");
        user.setPassword("12345");
        user.setPhoneNumber("+790390390387");

        User saved = userRepository.save(user);

        mockMvc.perform(get("/users-api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alex@test.com"));
    }


}


























