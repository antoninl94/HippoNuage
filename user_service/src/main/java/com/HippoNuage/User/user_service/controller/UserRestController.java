package com.HippoNuage.User.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
        @RequestBody UserUpdateDto updateDto, 
        @RequestHeader("Authorization") String authHeader) throws Exception {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Il manque ton jeton d'authentification, chevalier !");
        }
        String token = authHeader.substring((7));
        return this.userFacade.update(updateDto, token);
    }
}
