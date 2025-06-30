package com.example.materabank.infra.controller.dto.request;

import com.example.materabank.core.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record UserRequestDTO(
        @Schema(description = "Login do usuário", example = "usuario2")
        @NotBlank String login,

        @Schema(description = "Senha do usuário", example = "senhaSegura123")
        @NotBlank String password,

        @Schema(description = "Papel do usuário no sistema", example = "ADMIN")
        @NotNull UserRole role,

        @Schema(description = "Nome completo do usuário", example = "João da Silva")
        @NotBlank String fullName,

        @Schema(description = "CPF do usuário", example = "54588067001")
        @NotBlank @CPF String cpf,

        @Schema(description = "Data de nascimento", example = "1990-05-20")
        @NotNull @Past LocalDate birthDate
) {}

