package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {
    @Email
    @NotBlank
    @Size(min = 5, message = "L'email doit contenir au moins 5 caractères")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "Format de l'adresse email invalide"
    )
    private String newEmail;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    private String newPassword;

    //Getters et Setters
    public String getNewEmail() {

        return this.newEmail;
    }

    public void setNewEmail(String value) {
        this.newEmail = value;
    }

    public void setNewPassword(String value) {
        this.newPassword = value;
    }

    public String getNewPassword() {
        return newPassword;
    }

}
