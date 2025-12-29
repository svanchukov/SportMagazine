package ru.svanchukov.user.User_Service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.repository.UserRepository;
import ru.svanchukov.user.User_Service.service.UsersService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsersService usersService;

    @InjectMocks
    private CreateNewUserDTO createNewUserDTO;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@example.com");
        user.setPhoneNumber("+70000000000");
        return user;
    }

    @Test
    @DisplayName("Проверка метода __getUserByID__ если пользователь есть")
    void findById_whenUserExists_returnsUserDTO() {
        User u = sampleUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<UserDTO> result = usersService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("ivan@example.com");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Проверка метода __getUserByID__ если пользователя нет")
    void findById_whenUserNotFound_returnsEmptyOptional() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = usersService.getUserById(2L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Создание пользователя с помощью CreateUserDTO")
    void createUser_withCreateUserDTO() {

        CreateNewUserDTO userDTO = new CreateNewUserDTO();
        userDTO.setName("Ivan");
        userDTO.setEmail("ivan@example.com");
        userDTO.setPhoneNumber("+79998887766");

        User saved = new User();
        saved.setId(1L);
        saved.setName(userDTO.getName());
        saved.setEmail(userDTO.getEmail());
        saved.setPhoneNumber(userDTO.getPhoneNumber());

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDTO result = usersService.saveUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ivan@example.com");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Проверка метода __findAll__ если пользователей нет")
    void findAllUsers() {
        CreateNewUserDTO dto1 = new CreateNewUserDTO();
        dto1.setName("Petya");
        dto1.setEmail("petya228@yandex.ru");
        dto1.setPhoneNumber("+79049089089");

        CreateNewUserDTO dto2 = new CreateNewUserDTO();
        dto2.setName("Sasha");
        dto2.setEmail("sasha@yandex.ru");
        dto2.setPhoneNumber("+79039089089");

        User user1 = new User();
        user1.setId(1L);
        user1.setName(dto1.getName());
        user1.setEmail(dto1.getEmail());
        user1.setPhoneNumber(dto1.getPhoneNumber());

        User user2 = new User();
        user2.setId(2L);
        user2.setName(dto2.getName());
        user2.setEmail(dto2.getEmail());
        user2.setPhoneNumber(dto2.getPhoneNumber());

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = usersService.findAll();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getName()).isEqualTo("Petya");
        assertThat(result.get(0).getEmail()).isEqualTo("petya228@yandex.ru");

        assertThat(result.get(1).getName()).isEqualTo("Sasha");
        assertThat(result.get(1).getEmail()).isEqualTo("sasha@yandex.ru");

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Удаление пользователя по id")
    void deleteUserById() {

        when(userRepository.existsById(1L)).thenReturn(true);

        usersService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}



























