package com.example.materabank.infra.controller;

import com.example.materabank.application.UserUseCase;
import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.enums.UserRole;
import com.example.materabank.infra.controller.dto.request.UserRequestDTO;
import com.example.materabank.infra.controller.dto.response.UserResponseDTO;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de testes: UserController")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserUseCase userUseCase;

    @Test
    @Description("Deve criar um usuário e retornar status 201 com o corpo da resposta")
    void shouldCreateUserSuccessfully() {
        val request = buildUserRequestDTO();
        val response = new UserResponseDTO("1", "usuario1", "Fulano da Silva", UserRole.USER);

        when(userUseCase.create(request)).thenReturn(response);

        val result = userController.createUser(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(userUseCase).create(request);
    }

    @Test
    @Description("Deve retornar lista de usuários com status 200")
    void shouldListAllUsers() {
        val userList = List.of(new UserResponseDTO("1", "usuario1", "Fulano da Silva", UserRole.USER));
        when(userUseCase.listAll()).thenReturn(userList);

        val result = userController.listAllUsers();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(userList, result.getBody());
        verify(userUseCase).listAll();
    }

    @Test
    @Description("Deve retornar um usuário específico pelo ID com status 200")
    void shouldGetUserByIdSuccessfully() {
        val response = new UserResponseDTO("1", "usuario1", "Fulano da Silva", UserRole.USER);
        when(userUseCase.findById("1")).thenReturn(response);

        val result = userController.findById("1");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(userUseCase).findById("1");
    }

    @Test
    @Description("Deve retornar 404 caso o ID do usuário não exista")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userUseCase.findById("not_found")).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> userController.findById("not_found"));
        verify(userUseCase).findById("not_found");
    }

    @Test
    @Description("Deve atualizar os dados do usuário e retornar status 200")
    void shouldUpdateUserSuccessfully() {
        val request = buildUserRequestDTO();
        val response = new UserResponseDTO("1", "updatedUser", "Novo Nome", UserRole.ADMIN);

        when(userUseCase.update("1", request)).thenReturn(response);

        val result = userController.updateUser("1", request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(userUseCase).update("1", request);
    }

    @Test
    @Description("Deve retornar 404 ao tentar atualizar um usuário inexistente")
    void shouldReturnNotFoundWhenUpdatingNonExistingUser() {
        val request = buildUserRequestDTO();
        when(userUseCase.update("999", request)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> userController.updateUser("999", request));
        verify(userUseCase).update("999", request);
    }

    @Test
    @Description("Deve excluir o usuário e retornar status 204")
    void shouldDeleteUserSuccessfully() {
        doNothing().when(userUseCase).delete("1");

        val result = userController.deleteUser("1");

        assertEquals(204, result.getStatusCodeValue());
        verify(userUseCase).delete("1");
    }

    @Test
    @Description("Deve retornar 404 ao tentar excluir um usuário inexistente")
    void shouldReturnNotFoundWhenDeletingNonExistingUser() {
        doThrow(new NotFoundException("User not found")).when(userUseCase).delete("999");

        assertThrows(NotFoundException.class, () -> userController.deleteUser("999"));
        verify(userUseCase).delete("999");
    }

    private UserRequestDTO buildUserRequestDTO() {
        return new UserRequestDTO(
                "userTest",
                "senha123",
                UserRole.ADMIN,
                "64816221077",
                "Fulano da Silva",
                LocalDate.of(1990, 1, 1)
        );
    }
}
