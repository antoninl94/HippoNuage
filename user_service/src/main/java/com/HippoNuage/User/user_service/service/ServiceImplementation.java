package com.HippoNuage.User.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        if (loginDto.getEmail() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required.");
        }
        if (loginDto.getPassword() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Password is required.");
        }
        Optional<User> userOptional = this.userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Utilisateur non trouvé");
        }
        User user = userOptional.get();
        if (this.passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.ok("Bonjour Chevalier!");
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Mot de passe, CHEVALIER!");
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        if (registerDto.getEmail() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required.");
        }
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        this.userRepository.save(user);
        return ResponseEntity.ok("Chevalier créé ! Pour Hipponuage !");
    }

    @Override
<<<<<<< HEAD
    public ResponseEntity<?> update(UserUpdateDto updateDto){
        if ((updateDto.getNewEmail() == null) && (updateDto.getNewPassword() == null)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Tu dois mettre quelque chose à jour, chevalier");
        }
        return ResponseEntity.ok("c'est faux il manque le JWT"); 
     }
=======
    public ResponseEntity<?> update(UserUpdateDto updateDto) {
        if ((updateDto.getNewEmail() == null) && (updateDto.getNewPassword() == null)) {
            return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Entrée invalide");
        };

        userRepository.findByEmail();
        return ResponseEntity.ok("bonjour");
    }
>>>>>>> main

    @Override
    public ResponseEntity<?> disconnect(String token) {
        System.out.println(token);
        return ResponseEntity.ok("bonjour");
    }
}
