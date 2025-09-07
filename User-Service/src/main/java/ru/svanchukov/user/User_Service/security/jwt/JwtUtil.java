package ru.svanchukov.user.User_Service.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
 * Утилитарный класс для работы с JWT.
 * Генерация, проверка и получение информации из токена.
 */
@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /** Секретный ключ для подписи токена (32 байта) */
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "ec6c6ad944cee89c2348a4fde22e37bc".getBytes(StandardCharsets.UTF_8)
    );

    /** Время жизни токена в миллисекундах (1 час) */
    private static final long EXPIRATION_TIME = 3_600_000;

    public JwtUtil() { }

    /**
     * Генерирует JWT-токен для указанного email.
     * @param email email пользователя
     * @return JWT-токен
     */
    public String generateToken(final String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Получает email пользователя из JWT-токена.
     * @param token JWT-токен
     * @return email пользователя
     */
    public String getUsernameFromToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Проверяет валидность JWT-токена.
     * @param token JWT-токен
     * @return true если токен валиден, false если нет
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException
                 | UnsupportedJwtException | IllegalArgumentException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Невалидный JWT-токен: {}", e.getMessage());
            }
            return false;
        }
    }
}
