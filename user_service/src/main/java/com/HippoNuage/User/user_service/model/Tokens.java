package com.HippoNuage.User.user_service.model;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens")
public class Tokens {
    @Id
    @Column(name= "id")
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 256)
    private String tokenHash;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_blacklisted",  nullable = false)
    private boolean isBlacklisted = false;

    public Tokens() {}
    
    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        expiresAt = createdAt.plusHours(2);
    }

    //Getters & Setters
    public UUID getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String token_hash) {
        this.tokenHash = token_hash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean getIsBlacklisted() {
        return isBlacklisted;
    }

    public void setIsBlacklisted(boolean is_blacklisted) {
        this.isBlacklisted = is_blacklisted;
    }
}
