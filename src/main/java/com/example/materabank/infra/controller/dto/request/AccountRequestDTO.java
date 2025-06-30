package com.example.materabank.infra.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AccountRequestDTO(
        @Schema(description = "UUID do usuário", example = "c46c3f4a-3e87-4e02-a9ec-0b9c19e57389")
        @NotBlank String userId
) {}

