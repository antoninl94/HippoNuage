package com.HippoNuage.User.user_service;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.HippoNuage.User.user_service.config.JWTConfig;
import com.HippoNuage.User.user_service.model.BaseModel;
import com.HippoNuage.User.user_service.model.Tokens;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.TokenRepository;
import com.HippoNuage.User.user_service.service.TokenImplementation;

@ExtendWith(MockitoExtension.class)
public class JWTConfigTest {

    private User goodUser;

    @Mock
    private TokenImplementation tokenImplementation;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private JWTConfig jwtConfig;

    @BeforeEach
    void setup() throws Exception {
        // Injecte une valeur privée pour jwtSecret
        Field jwtSecretField = JWTConfig.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(jwtConfig, "maCleSecreteTest123456789012345678901234");

        User user = new User();
        Field idField = BaseModel.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, UUID.randomUUID());
        user.setEmail("valid@example.com");
    }

    @Test
    void shoudGenerateTokenForGoodUser() throws NoSuchFieldException, IllegalAccessException {
        Field jwtSecretField = JWTConfig.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(jwtConfig, "maCleSecreteTest123456789012345678901234");
        goodUser = new User();
        goodUser.setEmail("valid@example.com");
        goodUser.setPassword("correctPassword1324");
        Field idField = BaseModel.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(goodUser, UUID.randomUUID());

        when(tokenImplementation.hashToken(anyString())).thenReturn("fakeHashedToken");

        String token = jwtConfig.generateToken(goodUser);

        assertNotNull(token);
        verify(tokenImplementation).hashToken(token);

        ArgumentCaptor<Tokens> tokenCaptor = ArgumentCaptor.forClass(Tokens.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        Tokens saved = tokenCaptor.getValue();
        assertEquals("fakeHashedToken", saved.getTokenHash());
    }

    @Test
    void shouldFailWhenUserIdIsNull() throws Exception {
        User user = new User();
        Field idField = BaseModel.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, null);

        assertThrows(NullPointerException.class, () -> jwtConfig.generateToken(user));
    }
}
