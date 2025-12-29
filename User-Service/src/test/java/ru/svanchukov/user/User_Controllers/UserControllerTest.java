package ru.svanchukov.user.User_Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.svanchukov.user.User_Service.UserServiceApplication;
import ru.svanchukov.user.User_Service.controller.UserController;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.service.UserService;


import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = UserServiceApplication.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /users-api/users/{id} - успешное получение пользователя")
    void test_getUser_Success() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@yandex.ru");
        user.setPhoneNumber("+79689039078");
        user.setPassword("12345");

        when(userService.findById(1L))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/users-api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.email").value("alex@yandex.ru"))
                .andExpect(jsonPath("$.phoneNumber").value("+79689039078"));

    }

    @Test
    @DisplayName("GET /users-api/users/{id}/details - пользователь не найден")
    void test_getUserDetails_notFound() throws Exception {

        when(userService.findById(1L))
                .thenThrow(new UserNotFoundException("not found"));

        mockMvc.perform(get("/users-api/users/{id}/details", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /users-api/users/{id}/edit - успешное обновление")
    void test_updateUser_success() throws Exception {

        UpdateUserDTO request = new UpdateUserDTO();
        request.setName("newName");
        request.setEmail("alex@yandex.ru");
        request.setPhoneNumber("+79689039078");
        request.setPassword("12345");

        UpdateUserDTO response = new UpdateUserDTO();
        response.setName("newName");

        when(userService.updateUser(1L, request)).thenReturn(response);

        mockMvc.perform(patch("/users-api/users/{id}/edit", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"));
    }

    @Test
    @DisplayName("PATCH /users-api/users/{id}/edit - неуспешное обновление")
    void test_updateUser_notSuccess() throws Exception{

        UpdateUserDTO request = new UpdateUserDTO();
        request.setName("newName");
        request.setEmail("alex@yandex.ru");
        request.setPhoneNumber("+79689039078");
        request.setPassword("12345");

        when(userService.updateUser(200L, request))
                .thenThrow(new NoSuchElementException("Пользователь не найден"));

        mockMvc.perform(patch("/users-api/users/{id}/edit", 200)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь не найден"));
    }

    @Test
    @DisplayName("POST /users-api/users/{id}/delete - успешное удаление пользователя")
    void test_deleteUser_Success() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(post("/users-api/users/{id}/delete", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /users-api/users/{id}/delete - неуспешное удаление пользователя")
    void test_deleteUser_notSuccess() throws Exception {

        doThrow(new UserNotFoundException("Пользователь не найден"))
                .when(userService).deleteUser(300L);

        mockMvc.perform(post("/users-api/users/{id}/delete", 300))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользовател не найден"));
    }
}
























