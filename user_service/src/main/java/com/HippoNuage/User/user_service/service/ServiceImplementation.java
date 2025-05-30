package com.HippoNuage.User.user_service.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.config.JWTConfig;
import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.UserRepository;

@Service
public class ServiceImplementation implements UserFacade {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTConfig jwtConfig;

    @Autowired  // Dependency Injection -> Here We tell Spring to inject automatically a dependency
    public ServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTConfig jwtConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
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
            String token = this.jwtConfig.generateToken(user);
            return ResponseEntity.ok("Bonjour Chevalier!" + token);
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
    public ResponseEntity<?> update(UserUpdateDto updateDto, String token) throws Exception{
        if ((updateDto.getNewEmail() == null) || (updateDto.getNewPassword() == null)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Tu dois mettre quelque chose à jour, chevalier");
        }
        boolean JwtCheck = this.jwtConfig.validateToken(token, this.userRepository);
        if (!JwtCheck) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token non validen, sale gueux");
        }
        String userId = this.jwtConfig.extractUserId(token);
        Optional<User> user = this.userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Utilisateur non trouvé");
        }
        User finaluser = user.get();
        finaluser.setEmail(updateDto.getNewEmail());
        finaluser.setPassword(this.passwordEncoder.encode(updateDto.getNewPassword()));
        this.userRepository.save(finaluser);
        return ResponseEntity.ok("Votre profil a été modifié!");
    }

    @Override
    public ResponseEntity<?> disconnect(String token) {
        System.out.println(token);
        return ResponseEntity.ok("bonjour");
    }
}