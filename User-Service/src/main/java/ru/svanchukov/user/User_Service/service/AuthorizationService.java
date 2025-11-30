package ru.svanchukov.user.User_Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.svanchukov.user.User_Service.dto.CreateNewUserDTO;
import ru.svanchukov.user.User_Service.dto.LoginRequestDTO;
import ru.svanchukov.user.User_Service.entity.User;
import ru.svanchukov.user.User_Service.handler.UserNotFoundException;
import ru.svanchukov.user.User_Service.jwt.JwtUtil;
import ru.svanchukov.user.User_Service.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> register(CreateNewUserDTO createNewUserDTO) {
        if (userRepository.findByEmail(createNewUserDTO.getEmail()).isPresent()) {
            return Map.of("error", "Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setName(createNewUserDTO.getName());
        user.setEmail(createNewUserDTO.getEmail());
        user.setPhoneNumber(createNewUserDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(createNewUserDTO.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return Map.of("message", "Пользователь успешно зарегистрирован",
                "token", token);
    }

    public Map<String, String> login(LoginRequestDTO loginRequestDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );


        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким email нет"));

        String token = jwtService.generateToken(user.getEmail());

        return Map.of("message", "Вход выполнен успешно!",
                "token", token);
    }
}
