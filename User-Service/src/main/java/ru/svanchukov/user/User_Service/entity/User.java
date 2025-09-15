package ru.svanchukov.user.User_Service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Version
    @Column(nullable = false)
    private Long version; // Добавили для optimistic locking

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "jwt_token")
    private String jwtToken;

    public User() {
    }
}
