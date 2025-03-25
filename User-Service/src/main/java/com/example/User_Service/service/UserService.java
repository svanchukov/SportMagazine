package com.example.User_Service.service;


import com.example.User_Service.dto.CreateNewUserDTO;
import com.example.User_Service.dto.UpdateNewUserDTO;
import com.example.User_Service.dto.UserDTO;
import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Создание нового пользователя
    public UserDTO createUser(CreateNewUserDTO createNewUserDTO) {
        User user = mapCreateDTOToEntity(createNewUserDTO);
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    // Получение всех пользователей
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Получение пользователя по ID
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDTO(user);
    }

    // Обновление пользователя
    public UserDTO updateUser(Long id, UpdateNewUserDTO updateNewUserDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Обновляем поля
        existingUser.setName(updateNewUserDTO.getName());
        existingUser.setEmail(updateNewUserDTO.getEmail());
        existingUser.setAddress(updateNewUserDTO.getAddress());
        existingUser.setPhone(updateNewUserDTO.getPhone());

        User updatedUser = userRepository.save(existingUser);
        return mapToDTO(updatedUser);
    }

    // Удаление пользователя
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Маппинг CreateNewUserDTO в сущность User
    private User mapCreateDTOToEntity(CreateNewUserDTO createNewUserDTO) {
        User user = new User();
        user.setName(createNewUserDTO.getName());
        user.setEmail(createNewUserDTO.getEmail());
        user.setAddress(createNewUserDTO.getAddress());
        user.setPhone(createNewUserDTO.getPhone());
        return user;
    }

    // Маппинг сущности User в UserDTO
    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setPurchaseHistoryList(user.getPurchaseHistoryList());
        return userDTO;
    }
}