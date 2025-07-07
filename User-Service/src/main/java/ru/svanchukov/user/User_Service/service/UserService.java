package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.UpdateUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Получение пользователя по ID
    public Optional<UserDTO> findById(UUID id) {
        Optional<UserDTO> user = userRepository.findById(id).map(this::mapToDTO);
        if (user.isPresent()) {
            logger.info("Пользователь найден по ID {}: {}", id, user.get());
        } else {
            logger.warn("Пользователь с ID {} не найден", id);
        }
        return user;
    }

    // Получение данных для редактирования пользователя
    public UpdateUserDTO getUpdateUserDTO(UUID userId) {
        UserDTO userDTO = findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден"));

        // Создаем UpdateUserDTO и заполняем его данными из UserDTO
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName(userDTO.getName());
        updateUserDTO.setEmail(userDTO.getEmail());
        updateUserDTO.setPhoneNumber(userDTO.getPhoneNumber());
        updateUserDTO.setPassword(userDTO.getPassword());

        return updateUserDTO;
    }

    // Обновление пользователя
    public void updateUser(UUID id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("Пользователь с ID {} не найден для обновления", id);
            return new RuntimeException("Пользователь с ID " + id + " не найден");
        });

        user.setEmail(updateUserDTO.getEmail());
        user.setName(updateUserDTO.getName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setPassword(updateUserDTO.getPassword());

        userRepository.save(user);
        logger.info("Пользователь обновлён: {}", user);
    }

    // Удаление пользователя
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            logger.warn("Попытка удалить несуществующего пользователя с ID {}", id);
            throw new RuntimeException("Пользователь с ID " + id + " не найден");
        }

        userRepository.deleteById(id);
        logger.info("Пользователь с ID {} успешно удалён", id);
    }

    // Преобразование User в UserDTO
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setName(user.getName());
        dto.setPassword(user.getPassword());
        return dto;
    }
}
