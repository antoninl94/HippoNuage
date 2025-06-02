package com.HippoNuage.User.user_service.config;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JWTConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private static final long EXPIRATION_TIME = 7200000;
    private static final Logger logger = LoggerFactory.getLogger(JWTConfig.class);

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
        try {
            return Jwts.parserBuilder()
            .setSigningKey(jwtSecret.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            logger.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
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

    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
            .setSigningKey(jwtSecret.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();

        return expirationDate.before(new Date());
    }
}
