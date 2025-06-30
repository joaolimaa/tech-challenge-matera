package com.example.materabank.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de Testes: GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleInsufficientBalanceException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts/1/transactions");

        InsufficientBalanceException ex = new InsufficientBalanceException("Saldo insuficiente");

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientBalance(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Saldo insuficiente", response.getBody().get("message"));
        assertEquals("/accounts/1/transactions", response.getBody().get("path"));
    }

    @Test
    void shouldHandleNotFoundException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/accounts/999");

        NotFoundException ex = new NotFoundException("Conta não encontrada");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Conta não encontrada", response.getBody().get("message"));
        assertEquals("/accounts/999", response.getBody().get("path"));
    }

    @Test
    void shouldHandleDuplicatedRegisterException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/users");

        DuplicatedRegisterException ex = new DuplicatedRegisterException("Usuário já existe");

        ResponseEntity<Map<String, Object>> response = handler.handleDuplicatedRegister(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Usuário já existe", response.getBody().get("message"));
        assertEquals("/users", response.getBody().get("path"));
    }
}