package ru.svanchukov.user.User_Service.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.svanchukov.user.User_Service.security.jwt.JwtUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        String token = jwtUtil.generateToken(email);
        System.out.println("Generated token: " + token);
        // Кодируем токен для безопасной передачи в URL
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        // Перенаправляем с токеном в URL
        response.sendRedirect("http://localhost:8080/products?token=" + encodedToken);
    }
}