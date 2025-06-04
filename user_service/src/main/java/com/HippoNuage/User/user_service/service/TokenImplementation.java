package com.HippoNuage.User.user_service.service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.model.Tokens;
import com.HippoNuage.User.user_service.repository.TokenRepository;
import com.google.common.hash.Hashing;

@Service
public class TokenImplementation{

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenImplementation(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String hashToken(String token) {
        return Hashing.sha256()
            .hashString(token, StandardCharsets.UTF_8)
            .toString();
    }

    public boolean IsAllowedToken(String token) {
        Optional<Tokens> opttoken = this.tokenRepository.findByTokenHash(hashToken(token));
        if (opttoken.isEmpty()) {
            return false;
        }
        Tokens TokenResponse = opttoken.get();
        if (TokenResponse.IsBlacklisted()) {
            return false;
        }
        return true;
    }

    public void blacklistToken(Tokens token) {
        token.setIsBlacklisted(true);
        tokenRepository.save(token);
    }

    public Optional<Tokens> retrieveToken(String token) {
        Optional<Tokens> TokenObject = this.tokenRepository.findByTokenHash(hashToken(token));
        return TokenObject;
    }
}