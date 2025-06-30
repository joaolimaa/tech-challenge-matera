package com.example.materabank.infra.controller.dto.response;

import java.math.BigDecimal;

public record AccountResponseDTO(Long id, String userId, BigDecimal balance) {}
