package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с коллекцией пользователей.
 * Предоставляет методы для получения списка, создания и удаления пользователей.
 */
@Service
@RequiredArgsConstructor
public class UsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

    private final UserRepository userRepository;

    /**
     * Получение списка всех пользователей.
     * @return список UserDTO
     */
    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение пользователя по ID.
     * @param id идентификатор пользователя.
     */
    public Optional<UserDTO> getUserById(final Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO);
    }

    /**
     * Создание нового пользователя.
     *
     * @param createNewUserDTO DTO с данными для нового пользователя
     */
    public UserDTO saveUser(final CreateNewUserDTO createNewUserDTO) {
        final User user = new User();
        user.setEmail(createNewUserDTO.getEmail());
        user.setPhoneNumber(createNewUserDTO.getPhoneNumber());
        user.setName(createNewUserDTO.getName());


        userRepository.save(user);
        LOGGER.info("Создан новый пользователь: {}", user);

        return mapToDTO(user);
    }

    /**
     * Удаление пользователя по ID.
     * @param id идентификатор пользователя
     */
    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Попытка удалить несуществующего пользователя с ID {}", id);
            }
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }

        userRepository.deleteById(id);
        LOGGER.info("Пользователь с ID {} успешно удалён", id);
    }

    /**
     * Преобразует сущность User в DTO.
     * @param user сущность пользователя
     * @return UserDTO
     */
    private UserDTO mapToDTO(final User user) {
        final UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setName(user.getName());
        return dto;
    }
}
