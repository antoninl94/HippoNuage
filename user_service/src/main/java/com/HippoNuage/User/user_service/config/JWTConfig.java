package com.HippoNuage.User.user_service.config;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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

    // Token generation
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracting the user ID from the token
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

        Optional<User> userOpt = userRepository.findById(UUID.fromString(userId));
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        boolean isUserValid = userId.equals(user.getId().toString());

        return isUserValid && !isTokenExpired(token);
    }
}
