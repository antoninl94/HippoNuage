package com.HippoNuage.User.user_service.service;
import java.beans.BeanProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;

public interface UserFacade {
  public ResponseEntity<?> login(LoginDto loginDto);
  public ResponseEntity<?> register(RegisterDto registerDto);
  public ResponseEntity<?> update(UserUpdateDto updateDto);
  public ResponseEntity<?> disconnect(String token);
}