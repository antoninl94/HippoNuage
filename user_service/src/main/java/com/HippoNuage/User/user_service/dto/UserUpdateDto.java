package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {
    @Email
    private String NewEmail;

    @NotBlank
    private String newPassword;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
    )

    //Getters et Setters
    public String getNewEmail() {

        return this.NewEmail;
    }

    public void setNewEmail(String value) {
        this.NewEmail = value;
    }

    public void setNewPassword(String value) {
        this.NewEmail = value;
    }

    public String getNewPassword() {
        return newPassword;
    }

}
