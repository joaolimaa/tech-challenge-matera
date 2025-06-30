package com.example.materabank.infra.mapper;

import com.example.materabank.core.model.Account;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.response.AccountResponseDTO;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário para AccountMapper")
class AccountMapperTest {

    @Test
    @Description("Deve converter AccountRequestDTO e balance para entidade Account corretamente")
    void shouldMapToEntityCorrectly() {
        val dto = new AccountRequestDTO("user-123");
        val balance = BigDecimal.valueOf(250.75);

        Account account = AccountMapper.toEntity(dto, balance);

        assertEquals(dto.userId(), account.getUserId());
        assertEquals(balance, account.getBalance());
    }

    @Test
    @Description("Deve converter entidade Account para AccountResponseDTO corretamente")
    void shouldMapToResponseDtoCorrectly() {
        val account = Account.builder()
                .id(1L)
                .userId("user-456")
                .balance(BigDecimal.valueOf(999.99))
                .build();

        AccountResponseDTO dto = AccountMapper.toResponseDTO(account);

        assertEquals(account.getId(), dto.id());
        assertEquals(account.getUserId(), dto.userId());
        assertEquals(account.getBalance(), dto.balance());
    }

    @Test
    @Description("Deve lançar exceção ao tentar instanciar AccountMapper")
    void shouldThrowExceptionWhenInstantiatingUtilityClass() {
        assertThrows(UnsupportedOperationException.class, AccountMapper::new);
    }
}
