package ru.svanchukov.productservice.security.jwt;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.svanchukov.productservice.security.CustomAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        String authHeader = request.getHeader("Authorization");
        System.out.println("Request URL: " + request.getRequestURL() + "?" + request.getQueryString());
        System.out.println("Authorization header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token from header: " + token);
        }
        if (token == null) {
            token = request.getParameter("token");
            System.out.println("Token from parameter: " + token);
        }
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            System.out.println("Username from token: " + username);
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("UserDetails loaded: " + userDetails.getUsername());
            } catch (Exception e) {
                System.out.println("Failed to load UserDetails: " + e.getMessage());
                chain.doFilter(request, response);
                return;
            }
            CustomAuthenticationToken authentication = new CustomAuthenticationToken(userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication set with principal: " + authentication.getPrincipal());
        } else {
            System.out.println("Invalid or missing token: " + token);
        }
        chain.doFilter(request, response);
    }
}