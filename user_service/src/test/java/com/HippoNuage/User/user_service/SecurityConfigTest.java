package com.HippoNuage.User.user_service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.HippoNuage.User.user_service.config.SecurityConfig;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void shouldReturnAnEncryptedPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        System.out.println("Mon mot de passe hashé est : " + encoder.encode("monMotDePasse"));
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        assertTrue(encoder.matches("monMotDePasse", encoder.encode("monMotDePasse")));
    }

    @Test
    void shouldThrowExceptionWhenCostIsTooLow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new BCryptPasswordEncoder(3);
        });

        assertTrue(exception.getMessage().contains("Bad strength"));
    }
}
