package com.example.materabank.infra.gateway;

import com.example.materabank.core.exception.DuplicatedRegisterException;
import com.example.materabank.core.exception.GatewayException;
import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.UserRole;
import com.example.materabank.infra.repository.UserRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para UserGateway")
class UserGatewayTest {

    private UserRepository userRepository;
    private UserGateway userGateway;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userGateway = new UserGateway(userRepository);
    }

    private User buildUser() {
        val user = new User();
        user.setId("1");
        user.setLogin("login123");
        user.setPassword("senhaSegura");
        user.setRole(UserRole.USER);
        user.setCpf("12345678900");
        user.setFullName("Fulano da Silva");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    @DisplayName("Deve salvar usuário com sucesso se não existir duplicação")
    void shouldSaveUserSuccessfully() {
        val user = buildUser();

        when(userRepository.existsByLoginIgnoreCase(user.getLogin())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        val result = userGateway.save(user);

        assertEquals(user.getLogin(), result.getLogin());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar DuplicatedRegisterException ao tentar salvar login duplicado")
    void shouldThrowWhenUserAlreadyExists() {
        val user = buildUser();
        when(userRepository.existsByLoginIgnoreCase(user.getLogin())).thenReturn(true);

        assertThrows(DuplicatedRegisterException.class, () -> userGateway.save(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar GatewayException ao ocorrer erro ao salvar usuário")
    void shouldThrowGatewayExceptionOnSaveError() {
        val user = buildUser();
        when(userRepository.existsByLoginIgnoreCase(user.getLogin())).thenReturn(false);
        when(userRepository.save(user)).thenThrow(new RuntimeException("Erro"));

        assertThrows(GatewayException.class, () -> userGateway.save(user));
    }

    @Test
    @DisplayName("Deve retornar usuário pelo ID")
    void shouldFindById() {
        val user = buildUser();
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        val result = userGateway.findById("1");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar usuário pelo login")
    void shouldFindByLogin() {
        val user = buildUser();
        when(userRepository.findByLogin("login123")).thenReturn(Optional.of(user));

        val result = userGateway.findByLogin("login123");

        assertTrue(result.isPresent());
        assertEquals("login123", result.get().getLogin());
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void shouldListAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(buildUser()));

        val result = userGateway.listAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteSuccessfully() {
        doNothing().when(userRepository).deleteById("1");

        userGateway.delete("1");

        verify(userRepository).deleteById("1");
    }

    @Test
    @DisplayName("Deve lançar GatewayException ao deletar usuário com erro")
    void shouldThrowGatewayExceptionOnDelete() {
        doThrow(new RuntimeException("Erro ao deletar")).when(userRepository).deleteById("1");

        assertThrows(GatewayException.class, () -> userGateway.delete("1"));
    }
}
