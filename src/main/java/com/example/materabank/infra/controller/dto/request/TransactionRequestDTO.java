package com.example.materabank.infra.controller.dto.request;

import com.example.materabank.core.model.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @Schema(description = "Tipo da transação", example = "CREDIT")
        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @Schema(description = "Valor da transação", example = "150.00")
        @NotNull(message = "O valor da transação é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal amount
) {}