package com.example.materabank.infra.gateway;

import com.example.materabank.core.exception.DuplicatedRegisterException;
import com.example.materabank.core.exception.GatewayException;
import com.example.materabank.core.model.Account;
import com.example.materabank.infra.repository.AccountRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários para AccountGateway")
class AccountGatewayTest {

    private AccountRepository accountRepository;
    private AccountGateway accountGateway;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        accountGateway = new AccountGateway(accountRepository);
    }

    private Account buildAccount() {
        return Account.builder()
                .id(1L)
                .userId("user-123")
                .balance(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("Deve salvar conta com sucesso se não houver duplicidade")
    void shouldSaveAccountSuccessfully() {
        val account = buildAccount();

        when(accountRepository.existsByUserId(account.getUserId())).thenReturn(false);
        when(accountRepository.save(account)).thenReturn(account);

        val result = accountGateway.save(account);

        assertEquals(account.getId(), result.getId());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Deve lançar DuplicatedRegisterException se conta já existir")
    void shouldThrowWhenAccountAlreadyExists() {
        val account = buildAccount();

        when(accountRepository.existsByUserId(account.getUserId())).thenReturn(true);

        assertThrows(DuplicatedRegisterException.class, () -> accountGateway.save(account));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar GatewayException em erro inesperado ao salvar")
    void shouldThrowGatewayExceptionOnSaveError() {
        val account = buildAccount();

        when(accountRepository.existsByUserId(account.getUserId())).thenReturn(false);
        when(accountRepository.save(account)).thenThrow(new RuntimeException("Erro"));

        assertThrows(GatewayException.class, () -> accountGateway.save(account));
    }

    @Test
    @DisplayName("Deve atualizar conta com sucesso")
    void shouldUpdateAccountSuccessfully() {
        val account = buildAccount();

        when(accountRepository.save(account)).thenReturn(account);

        val result = accountGateway.update(account);

        assertEquals(account.getId(), result.getId());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Deve lançar GatewayException ao atualizar conta com erro")
    void shouldThrowGatewayExceptionOnUpdate() {
        val account = buildAccount();

        when(accountRepository.save(account)).thenThrow(new RuntimeException("Erro"));

        assertThrows(GatewayException.class, () -> accountGateway.update(account));
    }

    @Test
    @DisplayName("Deve encontrar conta por ID")
    void shouldFindById() {
        val account = buildAccount();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        val result = accountGateway.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(account.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Deve encontrar conta por userId")
    void shouldFindByUserId() {
        val account = buildAccount();

        when(accountRepository.findByUserId("user-123")).thenReturn(Optional.of(account));

        val result = accountGateway.findByUserId("user-123");

        assertTrue(result.isPresent());
        assertEquals(account.getUserId(), result.get().getUserId());
    }

    @Test
    @DisplayName("Deve listar todas as contas")
    void shouldListAllAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(buildAccount()));

        val result = accountGateway.listAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve deletar conta com sucesso")
    void shouldDeleteSuccessfully() {
        doNothing().when(accountRepository).deleteById(1L);

        accountGateway.delete(1L);

        verify(accountRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar GatewayException ao deletar com erro")
    void shouldThrowGatewayExceptionOnDelete() {
        doThrow(new RuntimeException("Erro")).when(accountRepository).deleteById(1L);

        assertThrows(GatewayException.class, () -> accountGateway.delete(1L));
    }
}
