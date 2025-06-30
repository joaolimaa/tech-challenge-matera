package com.example.materabank.infra.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Schema(description = "Login do usuário", example = "usuario1")
        @NotBlank
        String login,

        @Schema(description = "Senha do usuário", example = "senhaSegura123")
        @NotBlank
        String password
) {}
