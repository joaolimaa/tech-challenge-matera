package com.example.materabank.infra.security;

import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.UserRole;
import com.example.materabank.infra.gateway.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para SecurityFilter")
class SecurityFilterTest {

    private TokenService tokenService;
    private UserGateway userGateway;
    private SecurityFilter securityFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        tokenService = mock(TokenService.class);
        userGateway = mock(UserGateway.class);
        securityFilter = new SecurityFilter(tokenService, userGateway);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    @Description("Deve autenticar o usuário quando o token JWT é válido")
    void shouldAuthenticateUserWithValidToken() throws Exception {
        val token = "valid.jwt.token";
        val login = "usuario123";
        val user = buildUser();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(login);
        when(userGateway.findByLogin(login)).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        val authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        assert authentication.getPrincipal().equals(user);
        assert authentication.getAuthorities().equals(user.getAuthorities());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Description("Não deve autenticar se login for em branco mesmo com token válido")
    void shouldNotAuthenticateWhenLoginIsBlank() throws Exception {
        val token = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn("   ");

        securityFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Description("Não deve autenticar se token for nulo ou não informado")
    void shouldNotAuthenticateWhenTokenIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Description("Não deve autenticar se o header Authorization não começar com 'Bearer '")
    void shouldNotAuthenticateWhenAuthorizationHeaderIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abc123");

        securityFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Description("Não deve autenticar se token for inválido ou login não encontrado")
    void shouldNotAuthenticateWhenTokenInvalid() throws Exception {
        val token = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(null); // token inválido

        securityFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Description("Deve registrar um aviso no log se ocorrer exceção durante autenticação")
    void shouldLogWarningOnAuthenticationFailure() throws Exception {
        val token = "valid.jwt.token";
        val login = "usuario123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(login);
        when(userGateway.findByLogin(login)).thenThrow(new RuntimeException("Erro"));

        securityFilter.doFilterInternal(request, response, filterChain);

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response);
    }

    private User buildUser() {
        val user = new User();
        user.setId("abc-123");
        user.setLogin("usuario123");
        user.setPassword("senha");
        user.setRole(UserRole.USER);
        user.setFullName("Fulano");
        user.setCpf("12345678900");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }
}
