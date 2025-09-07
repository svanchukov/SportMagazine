package ru.svanchukov.productservice.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Утилитный класс для работы с JWT токенами.
 * Содержит методы для генерации, валидации и получения информации
 * (например, email) из токена.
 */
@Component
public class JwtUtil {

    /** Секретный ключ для подписи токенов (32 символа). */
    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor("ec6c6ad944cee89c2348a4fde22e37bc".getBytes(StandardCharsets.UTF_8));

    /** Время жизни токена в миллисекундах (1 час). */
    private static final long EXPIRATION_TIME = 3600000;

    /** Логгер для вывода ошибок и служебной информации. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Генерация нового JWT токена по email.
     * В Product-Service метод оставлен только для полноты картины, в реальном использовании
     * токены создаются в User-Service.
     * @param email email пользователя
     * @return JWT токен в виде строки
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Извлекает email (subject) из JWT токена.
     * @param token строка с JWT токеном
     * @return email, сохранённый в subject токена
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяет корректность JWT токена.
     * @param token строка с JWT токеном
     * @return {@code true}, если токен валиден; {@code false}, если подпись или срок действия некорректны
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
