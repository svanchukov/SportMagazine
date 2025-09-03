package ru.svanchukov.user.User_Service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Сущность пользователя.
 * Соответствует таблице "User" в базе данных.
 */
@Entity
@Table(name = "\"User\"") // Указываем точное имя таблицы с учетом регистра и кавычек
@Getter
@Setter
public class User {

    /**
     * Уникальный идентификатор пользователя (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Можно использовать кастомный генератор UUID
    @Column(name = "id", columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "jwtToken")
    private String jwtToken;

    public User() {
    }
}
