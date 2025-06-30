package com.example.materabank.application;

import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.UserRole;
import com.example.materabank.infra.controller.dto.request.UserRequestDTO;
import com.example.materabank.infra.gateway.UserGateway;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.materabank.infra.mapper.UserMapper.toResponseDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de Testes: UserUseCase")
class UserUseCaseTest {

    private static final String USER_ID = "c46c3f4a-3e87-4e02-a9ec-0b9c19e57389";
    private UserGateway userGateway;
    private UserUseCase userUseCase;

    @BeforeEach
    void setup() {
        userGateway = mock(UserGateway.class);
        userUseCase = new UserUseCase(userGateway);
    }

    @Test
    @Description("Deve criar um usuário com a senha criptografada")
    void shouldCreateUserWithEncryptedPassword() {
        val request = buildRequest();
        val savedUser = buildUser(request);
        when(userGateway.save(any(User.class))).thenReturn(savedUser);

        val result = userUseCase.create(request);

        assertEquals(toResponseDTO(savedUser), result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).save(captor.capture());
        assertNotEquals("senha123", captor.getValue().getPassword()); // Check password is encrypted
    }

    @Test
    @Description("Deve listar todos os usuários")
    void shouldListAllUsers() {
        val users = List.of(buildUser(buildRequest()));
        when(userGateway.listAll()).thenReturn(users);

        val result = userUseCase.listAll();

        assertEquals(1, result.size());
        assertEquals("usuario1", result.get(0).login());
    }

    @Test
    @Description("Deve encontrar um usuário pelo ID com sucesso")
    void shouldFindUserByIdSuccessfully() {
        val user = buildUser(buildRequest());
        when(userGateway.findById(USER_ID)).thenReturn(Optional.of(user));

        val result = userUseCase.findById(USER_ID);

        assertEquals(toResponseDTO(user), result);
    }

    @Test
    @Description("Deve lançar exceção ao buscar usuário inexistente por ID")
    void shouldThrowWhenUserNotFoundById() {
        when(userGateway.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userUseCase.findById("invalid"));
    }

    @Test
    @Description("Deve atualizar os dados de um usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        val request = buildRequest();
        when(userGateway.findById(USER_ID)).thenReturn(Optional.of(buildUser(request)));

        val updatedUser = buildUser(request);
        updatedUser.setFullName("Novo Nome");

        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        val result = userUseCase.update(USER_ID, request);

        assertEquals("Novo Nome", result.fullName());
    }

    @Test
    @Description("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void shouldThrowWhenUpdatingNonExistingUser() {
        val request = buildRequest();
        when(userGateway.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userUseCase.update(USER_ID, request));
    }

    @Test
    @Description("Deve excluir um usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        when(userGateway.findById(USER_ID)).thenReturn(Optional.of(buildUser(buildRequest())));

        userUseCase.delete(USER_ID);

        verify(userGateway).delete(USER_ID);
    }

    @Test
    @Description("Deve lançar exceção ao tentar excluir usuário inexistente")
    void shouldThrowWhenDeletingNonExistingUser() {
        when(userGateway.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userUseCase.delete(USER_ID));
    }

    private UserRequestDTO buildRequest() {
        return new UserRequestDTO(
                "usuario1",
                "senha123",
                UserRole.USER,
                "64816221077",
                "Fulano da Silva",
                LocalDate.of(1990, 1, 1)
        );
    }

    private User buildUser(UserRequestDTO dto) {
        val user = new User();
        user.setId(USER_ID);
        user.setLogin(dto.login());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        user.setCpf(dto.cpf());
        user.setFullName(dto.fullName());
        user.setBirthDate(dto.birthDate());
        return user;
    }
}
