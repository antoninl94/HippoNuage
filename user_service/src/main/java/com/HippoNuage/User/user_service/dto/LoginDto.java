package com.HippoNuage.User.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    // getters et setters
}