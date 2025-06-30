package com.example.materabank.infra.mapper;

import com.example.materabank.core.model.User;
import com.example.materabank.infra.controller.dto.request.UserRequestDTO;
import com.example.materabank.infra.controller.dto.response.UserResponseDTO;

public class UserMapper {
    UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User toEntity(UserRequestDTO dto) {
        return User.builder()
                .login(dto.login())
                .password(dto.password())
                .role(dto.role())
                .cpf(dto.cpf())
                .fullName(dto.fullName())
                .birthDate(dto.birthDate())
                .build();
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getLogin(),
                user.getFullName(),
                user.getRole()
        );
    }
}
