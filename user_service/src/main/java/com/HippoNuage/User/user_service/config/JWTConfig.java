package com.HippoNuage.User.user_service.config;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.HippoNuage.User.user_service.model.Tokens;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.TokenRepository;
import com.HippoNuage.User.user_service.repository.UserRepository;
import com.HippoNuage.User.user_service.service.TokenImplementation;

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
    
    @Autowired
    private final TokenRepository tokenRepository;
    private final TokenImplementation tokenImplementation;

    private JWTConfig(TokenRepository tokenRepository, TokenImplementation tokenImplementation) {
        this.tokenRepository = tokenRepository;
        this.tokenImplementation = tokenImplementation;
    }

    // Génère un nouveau token
    public String generateToken(User user) {
        String generatedToken = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        String hashToken = this.tokenImplementation.hashToken(generatedToken);
        Tokens tokenEntity = new Tokens();
            tokenEntity.setTokenHash(hashToken);
            this.tokenRepository.save(tokenEntity);
            return generatedToken;
    }

    // Extrait l'ID utilisateur du token
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

    // Méthode pour vérifier la validité d'un token via la date et l'ID utilisateur
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

    // Méthode pour vérifier la date d'expiration du token
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
