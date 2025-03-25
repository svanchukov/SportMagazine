package com.example.User_Service.controller;

import com.example.User_Service.dto.CreateNewUserDTO;
import com.example.User_Service.dto.UpdateNewUserDTO;
import com.example.User_Service.dto.UserDTO;
import com.example.User_Service.service.KafkaService.KafkaService;
import com.example.User_Service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final KafkaService kafkaService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateNewUserDTO createNewUserDTO) {
        UserDTO userDTO = userService.createUser(createNewUserDTO);
        kafkaService.createUser(createNewUserDTO);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateNewUserDTO updateNewUserDTO) {
        UserDTO userDTO = userService.updateUser(id, updateNewUserDTO);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
