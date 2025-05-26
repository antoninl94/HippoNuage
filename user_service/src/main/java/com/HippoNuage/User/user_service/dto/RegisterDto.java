package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.*;

public class RegisterDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    // getters et setters
    public String getEmail() {
        return email;
}

    public void setEmail(String value) {
        this.email = value;
}
    public String getPassword(){
        return this.password;
    }

    public void setPassword(String value){
        this.password = value; 
    }
}