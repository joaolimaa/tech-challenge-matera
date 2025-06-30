package com.example.materabank.infra.controller.dto.response;

import com.example.materabank.core.model.enums.UserRole;

public record UserResponseDTO(String id, String login, String fullName, UserRole role) {}
