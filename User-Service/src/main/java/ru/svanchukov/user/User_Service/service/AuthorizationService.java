package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.LoginRequestDTO;
import ru.svanchukov.user.User_Service.dto.UserDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.jwt.JwtService;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Map<String, String> register(CreateNewUserDTO createNewUserDTO) {
        if (userRepository.findByEmail(createNewUserDTO.getEmail()).isPresent()) {
            return Map.of("error", "Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setName(createNewUserDTO.getName());
        user.setEmail(createNewUserDTO.getEmail());
        user.setPhoneNumber(createNewUserDTO.getPhoneNumber());
        user.setPassword(createNewUserDTO.getPassword());

        userRepository.save(user);

        return Map.of("message", "Пользователь успешно зарегистрирован");
    }

    public Map<String, String> login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким email нет"));

        if (!user.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new RuntimeException("Неверный email или password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return Map.of("message", "Вход выполнен успешно!",
                "token", token);
    }
}
