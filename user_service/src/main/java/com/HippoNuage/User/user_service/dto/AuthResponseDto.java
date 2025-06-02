package com.HippoNuage.User.user_service.dto;

public class AuthResponseDto {
    private final String message;
    private final String token;

    public AuthResponseDto(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
