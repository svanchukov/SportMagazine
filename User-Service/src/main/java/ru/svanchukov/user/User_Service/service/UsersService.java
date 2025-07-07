package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.repository.UserRepository;
import ru.svanchukov.user.User_Service.security.jwt.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Получение списка всех пользователей
    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll(); // Получаем всех пользователей
        return users.stream().map(this::mapToDTO).collect(Collectors.toList()); // Преобразуем в DTO
    }

    // Получение пользователя по ID
    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::mapToDTO); // Получаем пользователя по ID
    }

    // Сохранение нового пользователя
    public UserDTO saveUser(CreateNewUserDTO createNewUserDTO) {
        User user = new User();
        user.setEmail(createNewUserDTO.getEmail());
        user.setPhoneNumber(createNewUserDTO.getPhoneNumber());
        user.setName(createNewUserDTO.getName());
        user.setPassword(createNewUserDTO.getPassword());

        String token = jwtUtil.generateToken(user.getEmail());
        user.setJwtToken(token);

        userRepository.save(user); // Сохраняем пользователя
        return mapToDTO(user); // Возвращаем DTO нового пользователя
    }

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
        return dto; // Возвращаем DTO
    }
}
