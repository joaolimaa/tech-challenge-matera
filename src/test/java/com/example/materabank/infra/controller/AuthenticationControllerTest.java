package com.example.materabank.infra.controller;

import com.example.materabank.application.AuthenticationUseCase;
import com.example.materabank.infra.controller.dto.request.LoginRequestDTO;
import com.example.materabank.infra.controller.dto.response.LoginResponseDTO;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de testes: AuthenticationController")
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationUseCase authenticationUseCase;

    @Test
    @Description("Deve autenticar usuário com sucesso e retornar token")
    void shouldLoginSuccessfully() {
        val loginRequest = new LoginRequestDTO("usuario1", "senha123");
        val expectedToken = "mocked.jwt.token";

        when(authenticationUseCase.authenticate(loginRequest)).thenReturn(expectedToken);

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedToken, response.getBody().token());
        verify(authenticationUseCase).authenticate(loginRequest);
    }

    @Test
    @Description("Deve validar token com sucesso")
    void shouldValidateTokenSuccessfully() {
        val loginResponse = new LoginResponseDTO("mocked.jwt.token");
        val expectedValidationResult = "Token is valid";

        when(authenticationUseCase.validateToken(loginResponse)).thenReturn(expectedValidationResult);

        ResponseEntity<String> response = authenticationController.validateToken(loginResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedValidationResult, response.getBody());
        verify(authenticationUseCase).validateToken(loginResponse);
    }
}
