package com.HippoNuage.User.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;
import com.HippoNuage.User.user_service.service.UserFacade;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserRestController {

    private final UserFacade userFacade;

    @Autowired
    public UserRestController(UserFacade UserFacade) {
        this.userFacade = UserFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) {
        return this.userFacade.register(registerDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        return this.userFacade.login(loginDto);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(
        @RequestBody @Valid UserUpdateDto updateDto, 
        @RequestHeader("Authorization") String authHeader) throws Exception {

        // Vérifie la présence du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Il manque ton jeton d'authentification, chevalier !");
        }
        String token = authHeader.substring((7));
        return this.userFacade.update(updateDto, token);
    }

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnect(@RequestHeader("Authorization") String token) {
        return userFacade.disconnect(token);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return userFacade.verifyEmail(token);
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?>resendEmail(@RequestHeader("Authorization") String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token manquant ou mal formé");
        }
        String token = authHeader.substring((7));
        return userFacade.resendEmail(token);
    }
}
