package com.HippoNuage.file_service.service;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
