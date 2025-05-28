package com.HippoNuage.User.user_service.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTConfig {

    @Value("${Jwt.secret}")
    private String jwtSecret;

    @Autowired
    private final UserRepository userRepository;
    private static final long EXPIRATION_TIME = 7200000;

    public JWTConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserRepository userRepository) {
        String userId = extractUserId(token);
        Optionnal<
        return userId.equals(this.userRepository.findById(userId))
    }
}
