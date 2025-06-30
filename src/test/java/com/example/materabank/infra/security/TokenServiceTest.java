package com.example.materabank.infra.security;

import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.exception.TokenGenerationException;
import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.UserRole;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário para TokenService")
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() throws Exception {
        tokenService = new TokenService();
        Field field = TokenService.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(tokenService, "my-secret-key");
    }

    @Test
    @Description("Deve gerar um token JWT válido para o usuário")
    void shouldGenerateValidToken() {
        val user = buildUser();

        val token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @Description("Deve validar um token JWT válido e retornar o login")
    void shouldValidateTokenSuccessfully() {
        val user = buildUser();
        val token = tokenService.generateToken(user);

        val subject = tokenService.validateToken(token);

        assertEquals(user.getLogin(), subject);
    }

    @Test
    @Description("Deve retornar null ao validar token inválido")
    void shouldReturnNullForInvalidToken() {
        val result = tokenService.validateToken("invalid-token");

        assertNull(result);
    }

    @Test
    @Description("Deve lançar TokenGenerationException ao gerar token com erro")
    void shouldThrowExceptionWhenTokenGenerationFails() throws Exception {
        Field field = TokenService.class.getDeclaredField("secret");
        field.setAccessible(true);
        field.set(tokenService, "");

        assertThrows(TokenGenerationException.class, () -> tokenService.generateToken(buildUser()));
    }

    @Test
    @Description("Deve retornar o usuário logado do contexto de segurança")
    void shouldReturnLoggedUserFromSecurityContext() {
        val user = buildUser();
        val auth = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        val result = tokenService.getLoggedUser();

        assertEquals(user.getLogin(), result.getLogin());
    }

    @Test
    @Description("Deve lançar NotFoundException quando não houver usuário autenticado")
    void shouldThrowWhenUserNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(NotFoundException.class, () -> tokenService.getLoggedUser());
    }

    private User buildUser() {
        val user = new User();
        user.setId("uuid-123");
        user.setLogin("user123");
        user.setPassword("password");
        user.setRole(UserRole.USER);
        user.setCpf("12345678900");
        user.setFullName("Fulano de Tal");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }
}
