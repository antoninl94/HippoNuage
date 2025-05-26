package com.HippoNuage.User.user_service.service;
import org.springframework.http.ResponseEntity;
import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;

interface UserFacade {
  public ResponseEntity<?> login(LoginDto loginDto);
  public ResponseEntity<?> register(RegisterDto registerDto);
  public ResponseEntity<?> update(UserUpdateDto updateDto);
  public ResponseEntity<?> disconnect(String token);
}