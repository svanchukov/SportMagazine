package com.example.GateWay.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${spring.security.jwt.secret:change_me}")
    private String secret;

    @Value("${spring.security.jwt.expiration-ms:3600000}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateAccessToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}















