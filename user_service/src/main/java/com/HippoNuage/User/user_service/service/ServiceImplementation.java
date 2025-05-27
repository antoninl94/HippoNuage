package com.HippoNuage.User.user_service.service;
import org.hibernate.annotations.SourceType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.dto.*;
import com.HippoNuage.User.user_service.repository.UserRepository;
import com.HippoNuage.User.user_service.model.User;

@Service
public class ServiceImplementation implements UserFacade{

    private final UserRepository userRepository;
     @Autowired  // Dependency Injection -> Here We tell Spring to inject automatically a dependency
        public ServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
       public ResponseEntity<?> login(LoginDto loginDto) {
            String ApiEmail = loginDto.getEmail();
            userRepository.findByEmail(ApiEmail);
            return ResponseEntity.ok("Bonjour Chevalier!");
    }

    @Override
        public ResponseEntity<?> register(RegisterDto registerDto) {
            User user = new User();
            if (registerDto.getEmail() == null){
                System.out.println("ca va pas");
            }
            user.setEmail(registerDto.getEmail());
            user.setPassword(registerDto.getPassword());
            this.userRepository.save(user);
            return ResponseEntity.ok("Chevalier créé ! Pour Hipponuage !");
    }
  
    @Override
        public ResponseEntity<?> update(UserUpdateDto updateDto){
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