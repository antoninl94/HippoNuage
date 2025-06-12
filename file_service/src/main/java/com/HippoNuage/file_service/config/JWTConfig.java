package com.HippoNuage.file_service.config;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JWTConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private static final Logger logger = LoggerFactory.getLogger(JWTConfig.class);

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

    // Méthode pour vérifier la date d'expiration du token
    public boolean isTokenExpired(String token) throws Exception {
        try {
            Date expirationDate = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expirationDate.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
