package com.HippoNuage.User.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.UserRepository;

@Service
public class ServiceImplementation implements UserFacade {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired  // Dependency Injection -> Here We tell Spring to inject automatically a dependency
    public ServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> login(LoginDto loginDto) {
        String ApiEmail = loginDto.getEmail();
        userRepository.findByEmail(ApiEmail);
        return ResponseEntity.ok("Bonjour Chevalier!");
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        if (registerDto.getEmail() == null) {
            System.out.println("ca va pas");
        }
        
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        this.userRepository.save(user);
        return ResponseEntity.ok("Chevalier créé ! Pour Hipponuage !");
    }

    @Override
    public ResponseEntity<?> update(UserUpdateDto updateDto) {
        String ApiEmail = updateDto.getEmail();
        userRepository.findByEmail(ApiEmail);
        return ResponseEntity.ok("bonjour");
    }

    @Override
    public ResponseEntity<?> disconnect(String token) {
        System.out.println(token);
        return ResponseEntity.ok("bonjour");
    }
}
