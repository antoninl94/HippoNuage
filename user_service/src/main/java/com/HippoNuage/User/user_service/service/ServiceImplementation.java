package com.HippoNuage.User.user_service.service;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.HippoNuage.User.user_service.dto.*;
import com.HippoNuage.User.user_service.repository.UserRepository;

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
            return ResponseEntity.ok("bonjour");
    }

    @Override
        public ResponseEntity<?> register(RegisterDto registerDto) {
            String ApiEmail = registerDto.getEmail();
            userRepository.findByEmail(ApiEmail);
            return ResponseEntity.ok("bonjour");
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