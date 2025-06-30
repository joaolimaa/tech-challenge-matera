package com.example.materabank.infra.controller;

import com.example.materabank.application.AccountUseCase;
import com.example.materabank.core.exception.InsufficientBalanceException;
import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.TransactionType;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.request.TransactionListRequest;
import com.example.materabank.infra.controller.dto.request.TransactionRequestDTO;
import com.example.materabank.infra.controller.dto.response.AccountResponseDTO;
import com.example.materabank.infra.security.TokenService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de testes: AccountController")
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountUseCase accountUseCase;

    @Mock
    private TokenService tokenService;

    private static final String USER_ID = "c46c3f4a-3e87-4e02-a9ec-0b9c19e57389";

    @Test
    @Description("Deve criar conta com sucesso")
    void shouldCreateAccountSuccessfully() {
        val request = new AccountRequestDTO(USER_ID);
        val response = new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO);

        when(accountUseCase.create(request)).thenReturn(response);

        val result = accountController.createAccount(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(accountUseCase).create(request);
    }

    @Test
    @Description("Deve listar todas as contas com sucesso")
    void shouldListAllAccounts() {
        val list = List.of(new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO));
        when(accountUseCase.listAll()).thenReturn(list);

        val result = accountController.listAll();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(list, result.getBody());
        verify(accountUseCase).listAll();
    }

    @Test
    @Description("Deve retornar conta por ID com sucesso")
    void shouldReturnAccountById() {
        val response = new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO);
        when(accountUseCase.findById(1L)).thenReturn(response);

        val result = accountController.getById(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
        verify(accountUseCase).findById(1L);
    }

    @Test
    @Description("Deve retornar 404 se a conta não for encontrada")
    void shouldReturnNotFoundWhenAccountDoesNotExist() {
        when(accountUseCase.findById(99L)).thenThrow(new NotFoundException("Conta não encontrada"));

        assertThrows(NotFoundException.class, () -> accountController.getById(99L));
        verify(accountUseCase).findById(99L);
    }

    @Test
    @Description("Deve excluir conta com sucesso quando usuário é dono da conta")
    void shouldDeleteAccountSuccessfully() {
        val response = new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO);
        val mockUser = new User();
        mockUser.setId(USER_ID);

        when(tokenService.getLoggedUser()).thenReturn(mockUser);
        when(accountUseCase.findById(1L)).thenReturn(response);

        val result = accountController.deleteAccount(1L);

        assertEquals(204, result.getStatusCodeValue());
        verify(accountUseCase).delete(1L);
    }

    @Test
    @Description("Deve retornar 403 ao tentar excluir conta de outro usuário")
    void shouldReturnForbiddenWhenUserIsNotOwner() {
        val response = new AccountResponseDTO(1L, "outro-user-id", BigDecimal.ZERO);
        val mockUser = new User();
        mockUser.setId(USER_ID);

        when(tokenService.getLoggedUser()).thenReturn(mockUser);
        when(accountUseCase.findById(1L)).thenReturn(response);

        val result = accountController.deleteAccount(1L);

        assertEquals(403, result.getStatusCodeValue());
        verify(accountUseCase, never()).delete(any());
    }

    @Test
    @Description("Deve retornar o saldo com sucesso")
    void shouldReturnBalanceSuccessfully() {
        val response = new AccountResponseDTO(1L, USER_ID, new BigDecimal("150.50"));
        when(accountUseCase.findById(1L)).thenReturn(response);

        val result = accountController.getBalance(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(new BigDecimal("150.50"), result.getBody());
        verify(accountUseCase).findById(1L);
    }

    @Test
    @Description("Deve processar transações com sucesso quando usuário é dono")
    void shouldProcessTransactionsSuccessfully() {
        val mockUser = new User();
        mockUser.setId(USER_ID);

        val account = new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO);
        val transactions = List.of(new TransactionRequestDTO(TransactionType.CREDIT, new BigDecimal("100.00")));

        when(tokenService.getLoggedUser()).thenReturn(mockUser);
        when(accountUseCase.findById(1L)).thenReturn(account);

        val request = new TransactionListRequest(transactions);

        val result = accountController.processTransactions(1L, request);

        assertEquals(200, result.getStatusCodeValue());
        verify(accountUseCase).processTransactions(1L, transactions);
    }

    @Test
    @Description("Deve retornar 403 ao tentar processar transações de outra conta")
    void shouldReturnForbiddenWhenProcessingTransactionsFromAnotherUser() {
        val mockUser = new User();
        mockUser.setId(USER_ID);

        val account = new AccountResponseDTO(1L, "outro-user-id", BigDecimal.ZERO);
        val transactions = List.of(new TransactionRequestDTO(TransactionType.CREDIT, new BigDecimal("100.00")));

        when(tokenService.getLoggedUser()).thenReturn(mockUser);
        when(accountUseCase.findById(1L)).thenReturn(account);

        val request = new TransactionListRequest(transactions);

        val result = accountController.processTransactions(1L, request);

        assertEquals(403, result.getStatusCodeValue());
        verify(accountUseCase, never()).processTransactions(anyLong(), any());
    }

    @Test
    @Description("Deve lançar exceção se saldo for insuficiente")
    void shouldThrowExceptionIfBalanceIsInsufficient() {
        val mockUser = new User();
        mockUser.setId(USER_ID);

        val account = new AccountResponseDTO(1L, USER_ID, BigDecimal.ZERO);
        val transactions = List.of(new TransactionRequestDTO(TransactionType.DEBIT, new BigDecimal("100.00")));

        when(tokenService.getLoggedUser()).thenReturn(mockUser);
        when(accountUseCase.findById(1L)).thenReturn(account);
        doThrow(new InsufficientBalanceException("Saldo insuficiente")).when(accountUseCase).processTransactions(1L, transactions);

        val request = new TransactionListRequest(transactions);

        assertThrows(InsufficientBalanceException.class, () -> accountController.processTransactions(1L, request));
        verify(accountUseCase).processTransactions(1L, transactions);
    }
}
