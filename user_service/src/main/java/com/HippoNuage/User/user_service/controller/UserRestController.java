package com.HippoNuage.User.user_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.service.UserFacade;

@RestController
@RequestMapping("/user")
public class UserRestController {
    

    private final UserFacade userFacade;

    @Autowired
    public UserRestController(UserFacade UserFacade) {
        this.userFacade = UserFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        return this.userFacade.register(registerDto);
    }
}
