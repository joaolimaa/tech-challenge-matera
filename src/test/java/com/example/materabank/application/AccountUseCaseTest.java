package com.example.materabank.application;

import com.example.materabank.core.exception.InsufficientBalanceException;
import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.Account;
import com.example.materabank.core.model.enums.TransactionType;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.request.TransactionRequestDTO;
import com.example.materabank.infra.gateway.AccountGateway;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suíte de Testes: AccountUseCase")
@ExtendWith(MockitoExtension.class)
class AccountUseCaseTest {

    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private AccountGateway accountGateway;

    @InjectMocks
    private AccountUseCase accountUseCase;

    private Account buildAccount() {
        return Account.builder()
                .id(ACCOUNT_ID)
                .userId("user-123")
                .balance(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @Description("Deve criar uma nova conta")
    void shouldCreateAccount() {
        val request = new AccountRequestDTO("user-123");
        val saved = buildAccount();

        when(accountGateway.save(any(Account.class))).thenReturn(saved);

        val result = accountUseCase.create(request);

        assertEquals(saved.getId(), result.id());
        verify(accountGateway).save(any(Account.class));
    }

    @Test
    @Description("Deve listar todas as contas")
    void shouldListAllAccounts() {
        when(accountGateway.listAll()).thenReturn(List.of(buildAccount()));

        val result = accountUseCase.listAll();

        assertEquals(1, result.size());
        verify(accountGateway).listAll();
    }

    @Test
    @Description("Deve buscar conta por ID")
    void shouldFindById() {
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));

        val result = accountUseCase.findById(ACCOUNT_ID);

        assertEquals(ACCOUNT_ID, result.id());
        verify(accountGateway).findById(ACCOUNT_ID);
    }

    @Test
    @Description("Deve lançar exceção ao buscar conta inexistente")
    void shouldThrowWhenAccountNotFound() {
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountUseCase.findById(ACCOUNT_ID));
    }

    @Test
    @Description("Deve excluir uma conta com sucesso")
    void shouldDeleteAccount() {
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        doNothing().when(accountGateway).delete(ACCOUNT_ID);

        accountUseCase.delete(ACCOUNT_ID);

        verify(accountGateway).delete(ACCOUNT_ID);
    }

    @Test
    @Description("Deve lançar exceção ao tentar excluir conta inexistente")
    void shouldThrowOnDeleteNonexistentAccount() {
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountUseCase.delete(ACCOUNT_ID));
    }

    @Test
    @Description("Deve lançar NotFoundException ao processar transações para conta inexistente")
    void shouldThrowNotFoundExceptionWhenAccountDoesNotExistOnTransactionProcessing() {
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        val transactions = List.of(
                new TransactionRequestDTO(TransactionType.CREDIT, BigDecimal.valueOf(100))
        );

        val exception = assertThrows(NotFoundException.class,
                () -> accountUseCase.processTransactions(ACCOUNT_ID, transactions));

        assertTrue(exception.getMessage().contains("Conta não encontrada para o id"));
    }

    @Test
    @Description("Deve processar uma transação de débito com sucesso")
    void shouldProcessDebitTransactionSuccessfully() {
        val account = buildAccount();
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountGateway.update(any(Account.class))).thenReturn(account);

        val transactions = List.of(
                new TransactionRequestDTO(TransactionType.DEBIT, BigDecimal.valueOf(40))
        );

        accountUseCase.processTransactions(ACCOUNT_ID, transactions);

        assertEquals(BigDecimal.valueOf(60), account.getBalance());
        verify(accountGateway).update(account);
    }

    @Test
    @Description("Deve processar transações de crédito e débito com sucesso")
    void shouldProcessTransactionsSuccessfully() {
        val account = buildAccount();
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountGateway.update(any(Account.class))).thenReturn(account);

        val transactions = List.of(
                new TransactionRequestDTO(TransactionType.CREDIT, BigDecimal.valueOf(50)),
                new TransactionRequestDTO(TransactionType.DEBIT, BigDecimal.valueOf(30))
        );

        accountUseCase.processTransactions(ACCOUNT_ID, transactions);

        assertEquals(BigDecimal.valueOf(120), account.getBalance());
        verify(accountGateway).update(account);
    }

    @Test
    @Description("Deve lançar exceção ao tentar debitar valor maior que o saldo")
    void shouldThrowWhenDebitExceedsBalance() {
        val account = buildAccount();
        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        val transactions = List.of(
                new TransactionRequestDTO(TransactionType.DEBIT, BigDecimal.valueOf(150))
        );

        assertThrows(InsufficientBalanceException.class, () -> accountUseCase.processTransactions(ACCOUNT_ID, transactions));
    }

    @Test
    @Description("Deve lançar InsufficientBalanceException ao capturar IllegalArgumentException do método debit")
    void shouldThrowInsufficientBalanceExceptionWhenIllegalArgumentIsThrown() {
        val account = new FakeAccount();
        account.setId(ACCOUNT_ID);
        account.setUserId("user-123");
        account.setBalance(BigDecimal.valueOf(100));

        val transactions = List.of(
                new TransactionRequestDTO(TransactionType.DEBIT, BigDecimal.valueOf(150))
        );

        when(accountGateway.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        val exception = assertThrows(InsufficientBalanceException.class,
                () -> accountUseCase.processTransactions(ACCOUNT_ID, transactions));

        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    static class FakeAccount extends Account {
        @Override
        public void debit(BigDecimal amount) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }
}
