package com.example.materabank.application;

import com.example.materabank.core.model.User;
import com.example.materabank.infra.controller.dto.request.LoginRequestDTO;
import com.example.materabank.infra.controller.dto.response.LoginResponseDTO;
import com.example.materabank.infra.repository.UserRepository;
import com.example.materabank.infra.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationUseCase {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public String authenticate(LoginRequestDTO data) {
        val usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        try {
            val authentication = authenticationManager.authenticate(usernamePassword);
            val user = (User) authentication.getPrincipal();
            return tokenService.generateToken(user);
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário '{}': {}", data.login(), e.getMessage(), e);
            throw e;
        }
    }

    public String validateToken(LoginResponseDTO data) {
        String login = tokenService.validateToken(data.token());
        return (login != null && !login.isBlank())
                ? "Token válido. Usuário autenticado: " + login
                : "Token inválido ou expirado.";
    }
}
