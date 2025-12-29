package ru.svanchukov.user.User_Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.repository.UserRepository;
import ru.svanchukov.user.User_Service.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceApplicationTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Captor
	private ArgumentCaptor<User> userCaptor;

	private User sampleUser() {
		User user = new User();
		user.setId(1L);
		user.setName("Kolik");
		user.setEmail("kolik@yandex.ru");
		user.setPhoneNumber("+79000009090");
		return user;
	}

	@Test
	@DisplayName("Поиск пользователя по ID")
	void findUserByID_ifExists() {
			User user = sampleUser();
			when(userRepository.findById(1L)).thenReturn(Optional.of(user));

			Optional<UserDTO> result = userService.findById(1L);

			assertThat(result).isPresent();
			assertThat(result.get().getId()).isEqualTo(1L);
			assertThat(result.get().getName()).isEqualTo("Kolik");

			verify(userRepository).findById(1L);
	}

	@Test
	@DisplayName("findById — пользователь не найден")
	void findById_notFound() {
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		Optional<UserDTO> result = userService.findById(2L);

		assertThat(result).isEmpty();

		verify(userRepository).findById(2L);
	}

	@Test
	@DisplayName("возвращает DTO если пользователь есть")
	void getUpdateUserDTO() {
		User user = sampleUser();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UpdateUserDTO dto = userService.getUpdateUserDTO(1L);

		assertThat(dto).isNotNull();
		assertThat(dto.getName()).isEqualTo("Kolik");
		assertThat(dto.getEmail()).isEqualTo("kolik@yandex.ru");

		verify(userRepository).findById(1L);
	}

	@Test
	@DisplayName("возвращает исключение если DTO нет")
	void getUpdateUserDTO_ifNotFound() {
		when(userRepository.findById(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getUpdateUserDTO(5L))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessageContaining("Пользователб с ID 5 не найден");
	}

	@Test
	@DisplayName("обновление пользователя")
	void updateUser() {
		User user = sampleUser();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UpdateUserDTO updatedUser = new UpdateUserDTO();
		updatedUser.setName("Kol");
		updatedUser.setEmail("kol@gmail.com");
		updatedUser.setPhoneNumber("+79089007623");

		UpdateUserDTO returned = userService.updateUser(1L, updatedUser);

		assertThat(returned.getName()).isEqualTo("Kol");
		assertThat(returned.getEmail()).isEqualTo("kol@gmail.com");

		verify(userRepository).findById(1L);
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("deleteUser — успешное удаление")
	void deleteUser_success() {
		when(userRepository.existsById(1L)).thenReturn(true);

		userService.deleteUser(1L);

		verify(userRepository).existsById(1L);
		verify(userRepository).deleteById(1L);
	}
}


































