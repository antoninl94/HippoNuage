package com.HippoNuage.User.user_service.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.HippoNuage.User.user_service.model.Tokens;

public interface TokenRepository extends JpaRepository<Tokens, UUID> {
    
    Optional<Tokens> findByTokenHash(String token_hash);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}