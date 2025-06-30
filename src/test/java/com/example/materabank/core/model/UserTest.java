package com.example.materabank.core.model;

import com.example.materabank.core.model.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste da entidade User")
class UserTest {

    private User buildUser(UserRole role) {
        return User.builder()
                .id("user-123")
                .cpf("12345678900")
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .login("usuario")
                .password("senha123")
                .role(role)
                .build();
    }

    @Test
    @Description("Deve retornar as autoridades corretas para ADMIN")
    void shouldReturnAdminAuthorities() {
        User user = buildUser(UserRole.ADMIN);

        List<GrantedAuthority> expected = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );

        assertEquals(expected, user.getAuthorities());
    }

    @Test
    @Description("Deve retornar apenas ROLE_USER para usuário comum")
    void shouldReturnUserAuthorities() {
        User user = buildUser(UserRole.USER);

        List<GrantedAuthority> expected = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        assertEquals(expected, user.getAuthorities());
    }

    @Test
    @Description("Deve retornar login como username")
    void shouldReturnLoginAsUsername() {
        User user = buildUser(UserRole.USER);
        assertEquals("usuario", user.getUsername());
    }

    @Test
    @Description("Deve sempre retornar true para os flags da conta")
    void shouldAlwaysReturnTrueForAccountFlags() {
        User user = buildUser(UserRole.USER);

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
