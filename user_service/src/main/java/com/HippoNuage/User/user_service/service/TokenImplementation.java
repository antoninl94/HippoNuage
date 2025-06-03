package com.HippoNuage.User.user_service.service;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
@Service
public class TokenImplementation{

    public String hashToken(String token) {
        return Hashing.sha256()
            .hashString(token, StandardCharsets.UTF_8)
            .toString();
    }
}