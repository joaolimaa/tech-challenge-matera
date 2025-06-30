package com.example.materabank.core.model;

import com.example.materabank.core.exception.InsufficientBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste da entidade Account")
class AccountTest {

    @Test
    @Description("Deve adicionar valor ao saldo com sucesso")
    void shouldCreditSuccessfully() {
        Account account = Account.builder()
                .id(1L)
                .userId("user123")
                .balance(BigDecimal.valueOf(100))
                .build();

        account.credit(BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
    }

    @Test
    @Description("Deve debitar valor do saldo com sucesso")
    void shouldDebitSuccessfully() {
        Account account = Account.builder()
                .id(1L)
                .userId("user123")
                .balance(BigDecimal.valueOf(200))
                .build();

        account.debit(BigDecimal.valueOf(80));

        assertEquals(BigDecimal.valueOf(120), account.getBalance());
    }

    @Test
    @Description("Deve lançar exceção ao tentar debitar mais do que o saldo")
    void shouldThrowExceptionWhenInsufficientBalance() {
        Account account = Account.builder()
                .id(1L)
                .userId("user123")
                .balance(BigDecimal.valueOf(50))
                .build();

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class,
                () -> account.debit(BigDecimal.valueOf(100)));

        assertTrue(exception.getMessage().contains("Saldo insuficiente para débito de"));
    }
}
