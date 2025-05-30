package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.*;

public class UserUpdateDto {
    @Email
    private String NewEmail;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    private String newPassword;

    //Getters et Setters
    public String getNewEmail() {
        return this.NewEmail;
    }

    public void setEmail(String value) {
        this.NewEmail = value;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}