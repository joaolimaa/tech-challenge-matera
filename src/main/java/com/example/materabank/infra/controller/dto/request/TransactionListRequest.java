package com.example.materabank.infra.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TransactionListRequest(
        @NotEmpty(message = "É necessário ao menos uma transação")
        List<@Valid TransactionRequestDTO> transactions
) {}

