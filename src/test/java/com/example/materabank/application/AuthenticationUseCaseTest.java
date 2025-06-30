package com.example.materabank.application;

import com.example.materabank.core.model.User;
import com.example.materabank.infra.controller.dto.request.LoginRequestDTO;
import com.example.materabank.infra.controller.dto.response.LoginResponseDTO;
import com.example.materabank.infra.security.TokenService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de Testes: AuthenticationUseCase")
@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    @Test
    @Description("Deve autenticar o usuário e retornar um token válido")
    void shouldAuthenticateSuccessfully() {
        val login = "user1";
        val password = "pass123";
        val dto = new LoginRequestDTO(login, password);

        val mockUser = new User();
        mockUser.setLogin(login);

        val authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(mockUser)).thenReturn("mocked-token");

        val token = authenticationUseCase.authenticate(dto);

        assertEquals("mocked-token", token);
        verify(authenticationManager).authenticate(any());
        verify(tokenService).generateToken(mockUser);
    }

    @Test
    @Description("Deve lançar exceção ao falhar na autenticação")
    void shouldThrowExceptionOnAuthenticationFailure() {
        val dto = new LoginRequestDTO("invalidUser", "wrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        val exception = assertThrows(RuntimeException.class, () -> authenticationUseCase.authenticate(dto));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(tokenService);
    }

    @Test
    @Description("Deve validar token e retornar mensagem com o login do usuário")
    void shouldValidateTokenSuccessfully() {
        val dto = new LoginResponseDTO("valid-token");

        when(tokenService.validateToken("valid-token")).thenReturn("user123");

        val result = authenticationUseCase.validateToken(dto);

        assertTrue(result.contains("Token válido"));
        assertTrue(result.contains("user123"));
    }

    @Test
    @Description("Deve retornar mensagem de token inválido quando login estiver em branco")
    void shouldReturnInvalidTokenMessageWhenLoginIsBlank() {
        val dto = new LoginResponseDTO("valid-but-blank-login");

        when(tokenService.validateToken("valid-but-blank-login")).thenReturn("  "); // string em branco

        val result = authenticationUseCase.validateToken(dto);

        assertEquals("Token inválido ou expirado.", result);
    }

    @Test
    @Description("Deve retornar mensagem de token inválido ou expirado")
    void shouldReturnInvalidTokenMessage() {
        val dto = new LoginResponseDTO("invalid-token");

        when(tokenService.validateToken("invalid-token")).thenReturn(null);

        val result = authenticationUseCase.validateToken(dto);

        assertEquals("Token inválido ou expirado.", result);
    }
}
