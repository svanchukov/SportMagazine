package ru.svanchukov.user.IntegrationTests.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import ru.svanchukov.user.User_Service.UserServiceApplication;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.Optional;

@ContextConfiguration(classes = UserServiceApplication.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Интеграционный тест репозитория: нахождение пользователя по email")
    void findByEmail() {
        User user = new User();
        user.setEmail("alex@test.com");
        user.setName("Alex");
        user.setPassword("12345");

        userRepository.save(user);

        Optional<User> findUser = userRepository.findByEmail("alex@test.com");

        Assertions.assertThat(findUser).isPresent();
        Assertions.assertThat(findUser.get().getEmail())
                .isEqualTo("alex@test.com");
    }
}
