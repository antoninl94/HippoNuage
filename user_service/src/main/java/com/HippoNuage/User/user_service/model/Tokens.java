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
    private String token_hash;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @Column(name = "expires_at", updatable = false)
    private LocalDateTime expires_at;

    @Column(name = "is_blacklisted",  nullable = false)
    private boolean is_blacklisted = false;

    public Tokens() {}
    
    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        created_at = LocalDateTime.now();
        expires_at = created_at.plusHours(2);
    }

    //Getters & Setters
    public UUID getId() {
        return id;
    }

    public String getToken_hash() {
        return token_hash;
    }

    public void setToken_hash(String token_hash) {
        this.token_hash = token_hash;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getExpires_at() {
        return expires_at;
    }

    public boolean Is_blacklisted() {
        return is_blacklisted;
    }

    public void setIs_blacklisted(boolean is_blacklisted) {
        this.is_blacklisted = is_blacklisted;
    }
}