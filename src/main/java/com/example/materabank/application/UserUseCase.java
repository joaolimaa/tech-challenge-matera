package com.example.materabank.application;

import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.User;
import com.example.materabank.infra.controller.dto.request.UserRequestDTO;
import com.example.materabank.infra.controller.dto.response.UserResponseDTO;
import com.example.materabank.infra.gateway.UserGateway;
import com.example.materabank.infra.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.materabank.infra.mapper.UserMapper.toEntity;
import static com.example.materabank.infra.mapper.UserMapper.toResponseDTO;

@Service
@RequiredArgsConstructor
public class UserUseCase {
    private final UserGateway userGateway;

    public UserResponseDTO create(UserRequestDTO dto) {
        val encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
        val user = User.builder()
                .cpf(dto.cpf())
                .fullName(dto.fullName())
                .birthDate(dto.birthDate())
                .login(dto.login())
                .password(encryptedPassword)
                .role(dto.role())
                .build();
        val savedUser = userGateway.save(user);
        return toResponseDTO(savedUser);
    }

    public List<UserResponseDTO> listAll() {
        return userGateway.listAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    public UserResponseDTO findById(String id) {
        val user = userGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado para o id: " + id));
        return toResponseDTO(user);
    }

    public UserResponseDTO update(String id, UserRequestDTO dto) {
        if (userGateway.findById(id).isEmpty()) {
            throw new NotFoundException("Usuário não encontrado para o id: " + id);
        }

        val user = toEntity(dto);
        user.setId(id);
        return toResponseDTO(userGateway.save(user));
    }

    public void delete(String id) {
        if (userGateway.findById(id).isEmpty()) {
            throw new NotFoundException("Usuário não encontrado para o id: " + id);
        }

        userGateway.delete(id);
    }
}
