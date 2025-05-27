package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Format du mot de passe invalide"
    )
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
