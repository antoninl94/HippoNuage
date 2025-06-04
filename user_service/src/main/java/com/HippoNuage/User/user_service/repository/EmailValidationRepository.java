package com.HippoNuage.User.user_service.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.HippoNuage.User.user_service.model.EmailValidationToken;

public interface EmailValidationRepository extends JpaRepository<EmailValidationToken, UUID>{
    Optional<EmailValidationToken>findByToken(String token);
    //Pour nettoyer les tokens expirés non utilisés
    void deleteByUsedFalseAndExpiresAtBefore(LocalDateTime now);
    //Pour nettoyer les tokens utilisés depuis plus d'une certaine durée (on les conserve pour les logs)
    void deleteByUsedTrueAndExpiresAtBefore(LocalDateTime date);
}