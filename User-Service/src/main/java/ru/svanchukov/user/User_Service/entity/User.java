package ru.svanchukov.user.User_Service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

/**
 * Сущность пользователя.
 * Соответствует таблице "User" в базе данных.
 */
@Entity
@Table(name = "\"user\"") // Указываем точное имя таблицы с учетом регистра и кавычек
@Getter
@Setter
@ToString
public class User {

    /**
     * Уникальный идентификатор пользователя (BIGINT).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version; // Добавили для optimistic locking

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    public User() {
    }
}
