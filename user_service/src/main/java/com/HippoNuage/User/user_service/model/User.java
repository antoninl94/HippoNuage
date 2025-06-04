package com.HippoNuage.User.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseModel{

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "validated_email", nullable = false)
    private boolean validatedEmail = false;

    //Getters & Setters
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public boolean getValidatedEmail() {
        return this.validatedEmail;
    }
    
    public void setValidatedEmail(boolean isValid){
        this.validatedEmail = isValid;
    }
}
