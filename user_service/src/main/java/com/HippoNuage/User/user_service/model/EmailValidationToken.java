package com.HippoNuage.User.user_service.model;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_validation_tokens")
public class EmailValidationToken {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used",  nullable = false)
    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_email_validation_user"))
    private User user;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        expiresAt = createdAt.plusHours(1);
    }

    //Getters & Setters
    public UUID getId(){
        return this.id;
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String newToken){
        this.token = newToken;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }

    public LocalDateTime getExpiresAt(){
        return this.expiresAt;
    }

    public boolean getUsed(){
        return this.used;
    }
    
    public void setUsed(boolean usedValue){
        this.used = usedValue;
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User newUser){
        this.user = newUser;
    }
}
