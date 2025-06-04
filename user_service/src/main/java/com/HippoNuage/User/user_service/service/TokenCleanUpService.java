package com.HippoNuage.User.user_service.service;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.repository.TokenRepository;

import jakarta.transaction.Transactional;

@Service
public class TokenCleanUpService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenCleanUpService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Scheduled(fixedDelay = 21600000) // Delai de 6h pour le nettoyage en base des tokens expirés
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiresAtBefore(now);
    }
}