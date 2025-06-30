package com.example.materabank.infra.mapper;

import com.example.materabank.core.model.Account;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.response.AccountResponseDTO;

import java.math.BigDecimal;

public class AccountMapper {
    AccountMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Account toEntity(AccountRequestDTO dto, BigDecimal balance) {
        return Account.builder()
                .userId(dto.userId())
                .balance(balance)
                .build();
    }

    public static AccountResponseDTO toResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getUserId(),
                account.getBalance()
        );
    }
}
