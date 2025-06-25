package com.HippoNuage.file_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final String jwtSecret;

    public SecurityConfig(@org.springframework.beans.factory.annotation.Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(org.springframework.security.config.Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes();
        return NimbusJwtDecoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256")).build();
    }
}
