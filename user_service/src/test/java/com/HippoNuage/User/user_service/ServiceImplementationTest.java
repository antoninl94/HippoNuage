package com.HippoNuage.User.user_service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.HippoNuage.User.user_service.config.JWTConfig;
import com.HippoNuage.User.user_service.dto.AuthResponseDto;
import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.TokenRepository;
import com.HippoNuage.User.user_service.repository.UserRepository;
import com.HippoNuage.User.user_service.service.EmailValidationService;
import com.HippoNuage.User.user_service.service.ServiceImplementation;
import com.HippoNuage.User.user_service.service.TokenImplementation;

@ExtendWith(MockitoExtension.class)
public class ServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTConfig jwtConfig;

    @Mock
    private TokenImplementation tokenImplementation;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private ServiceImplementation service;

    @Test
    void shouldLoginSuccessfully() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@email.com");
        loginDto.setPassword("password");
        User mockUser = new User();
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtConfig.generateToken(mockUser)).thenReturn("mockedToken");

        ResponseEntity<?> response = service.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDto);
        AuthResponseDto authResponse = (AuthResponseDto) response.getBody();
        assertEquals("Bonjour Sire!", authResponse.getMessage());
        assertEquals("mockedToken", authResponse.getToken());
    }

    @Test
    void shouldNotLoginWithSqli() {
        // Test injection SQL n°1
        LoginDto login = new LoginDto();
        login.setEmail("' OR 1=1 --");
        login.setPassword("irrelevant");
        when(userRepository.findByEmail("' OR 1=1 --")).thenReturn(Optional.empty());
        ResponseEntity<?> response = service.login(login);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Utilisateur non trouvé", response.getBody());

        // Test Injection SQL n°2
        LoginDto login2 = new LoginDto();
        login2.setEmail("' UNION SELECT * FROM users --");
        login2.setPassword("irrelevant");
        when(userRepository.findByEmail("' UNION SELECT * FROM users --")).thenReturn(Optional.empty());
        ResponseEntity<?> response2 = service.login(login);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals("Utilisateur non trouvé", response2.getBody());
    }

    @Test
    void shouldLoginFail() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@email.com");
        loginDto.setPassword("wrongPassword");
        User mockUser = new User();
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        ResponseEntity<?> response = service.login(loginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Mot de passe, CHEVALIER!", response.getBody());
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@email.com");
        registerDto.setPassword("password");
        User mockUser = new User();
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtConfig.generateToken(any(User.class))).thenReturn("mockedToken");

        ResponseEntity<?> response = service.register(registerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDto);

        AuthResponseDto authResponse = (AuthResponseDto) response.getBody();
        assertEquals("Chevalier adoubé! Pour Hipponuage ! Valide ton email pour accéder aux fonctionnalités !", authResponse.getMessage());
        assertEquals("mockedToken", authResponse.getToken());
    }

    @Test
    void shouldRegisterFail() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@email.com");
        registerDto.setPassword("password");
        User existingUser = new User();
        existingUser.setEmail("test@email.com");
        existingUser.setPassword("EncodedPassword");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> response = service.register(registerDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Chevalier déja existant !", response.getBody());
    }
}
