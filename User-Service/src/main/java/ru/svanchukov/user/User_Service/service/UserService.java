package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для получения, обновления и удаления пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    /**
     * Получение пользователя по ID.
     * @param id идентификатор пользователя
     */
    public Optional<UserDTO> findById(final UUID id) {
        final Optional<UserDTO> user = userRepository.findById(id).map(this::mapToDTO);

        if (user.isPresent()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Пользователь найден по ID {}: {}", id, user.get());
            }
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Пользователь с ID {} не найден", id);
            }
        }
        return user;
    }

    /**
     * Получение данных для редактирования пользователя.
     * @param userId идентификатор пользователя
     */
    public UpdateUserDTO getUpdateUserDTO(final UUID userId) {
        final UserDTO userDTO = findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        final UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName(userDTO.getName());
        updateUserDTO.setEmail(userDTO.getEmail());
        updateUserDTO.setPhoneNumber(userDTO.getPhoneNumber());
        updateUserDTO.setPassword(userDTO.getPassword());

        return updateUserDTO;
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id            идентификатор пользователя
     * @param updateUserDTO данные для обновления
     * @return
     */
    public UpdateUserDTO updateUser(final UUID id, final UpdateUserDTO updateUserDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("Пользователь с ID {} не найден для обновления", id);
                    }
                    return new UserNotFoundException("Пользователь с ID " + id + " не найден");
                });

        user.setEmail(updateUserDTO.getEmail());
        user.setName(updateUserDTO.getName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setPassword(updateUserDTO.getPassword());

        userRepository.save(user);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Пользователь обновлён: {}", user);
        }
        return updateUserDTO;
    }

    /**
     * Удаление пользователя по ID.
     * @param id идентификатор пользователя
     */
    public void deleteUser(final UUID id) {
        if (!userRepository.existsById(id)) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Попытка удалить несуществующего пользователя с ID {}", id);
            }
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }

        userRepository.deleteById(id);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Пользователь с ID {} успешно удалён", id);
        }
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
        dto.setPassword(user.getPassword());
        return dto;
    }
}
