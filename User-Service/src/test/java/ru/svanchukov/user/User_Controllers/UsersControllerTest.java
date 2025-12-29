package ru.svanchukov.user.User_Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.svanchukov.user.User_Service.UserServiceApplication;
import ru.svanchukov.user.User_Service.controller.UsersController;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.service.UsersService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = UserServiceApplication.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /users-api/users - получение всех пользователей")
    void test_getAllUsers() throws Exception {

        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setName("Alex");
        user1.setEmail("alex@yandex.ru");
        user1.setPhoneNumber("+79689039078");
        user1.setPassword("12345");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setEmail("maria@yandex.ru");
        user2.setPhoneNumber("+79161234567");
        user2.setPassword("54321");

        List<UserDTO> users = Arrays.asList(user1, user2);

        when(usersService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users-api/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alex"))
                .andExpect(jsonPath("$[0].email").value("alex@yandex.ru"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Maria"))
                .andExpect(jsonPath("$[1].email").value("maria@yandex.ru"));
    }

    @Test
    @DisplayName("GET /users-api/users - получение всех пользователей НЕУДАЧНОЕ")
    void test_testAllUsers_Empty() throws Exception {

        when(usersService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/users-api/users"))
                .andExpect(status().isNoContent());
    }
}








































