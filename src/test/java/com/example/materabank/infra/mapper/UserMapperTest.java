package com.example.materabank.infra.mapper;

import com.example.materabank.core.model.User;
import com.example.materabank.core.model.enums.UserRole;
import com.example.materabank.infra.controller.dto.request.UserRequestDTO;
import com.example.materabank.infra.controller.dto.response.UserResponseDTO;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário para UserMapper")
class UserMapperTest {

    @Test
    @Description("Deve converter UserRequestDTO para entidade User corretamente")
    void shouldMapToEntityCorrectly() {
        val dto = new UserRequestDTO(
                "login123",
                "senhaSegura",
                UserRole.USER,
                "12345678900",
                "Fulano da Silva",
                LocalDate.of(1995, 5, 15)
        );

        val user = UserMapper.toEntity(dto);

        assertEquals(dto.login(), user.getLogin());
        assertEquals(dto.password(), user.getPassword());
        assertEquals(dto.role(), user.getRole());
        assertEquals(dto.cpf(), user.getCpf());
        assertEquals(dto.fullName(), user.getFullName());
        assertEquals(dto.birthDate(), user.getBirthDate());
    }

    @Test
    @Description("Deve converter entidade User para UserResponseDTO corretamente")
    void shouldMapToResponseDtoCorrectly() {
        val user = User.builder()
                .id("abc123")
                .login("login123")
                .password("senhaSegura")
                .role(UserRole.ADMIN)
                .cpf("98765432100")
                .fullName("Maria Oliveira")
                .birthDate(LocalDate.of(1990, 10, 1))
                .build();

        UserResponseDTO dto = UserMapper.toResponseDTO(user);

        assertEquals(user.getId(), dto.id());
        assertEquals(user.getLogin(), dto.login());
        assertEquals(user.getFullName(), dto.fullName());
        assertEquals(user.getRole(), dto.role());
    }

    @Test
    @Description("Deve lançar exceção ao tentar instanciar UserMapper")
    void shouldThrowExceptionWhenInstantiatingUtilityClass() {
        assertThrows(UnsupportedOperationException.class, UserMapper::new);
    }
}
