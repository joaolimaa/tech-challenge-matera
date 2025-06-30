package com.example.materabank.infra.gateway;

import com.example.materabank.core.exception.DuplicatedRegisterException;
import com.example.materabank.core.exception.GatewayException;
import com.example.materabank.core.model.User;
import com.example.materabank.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserGateway {
    private static final Logger logger = LogManager.getLogger(UserGateway.class);
    private final UserRepository userRepository;

    public User save(User user) {
        try {
            val alreadyExistUser = userRepository.existsByLoginIgnoreCase(user.getLogin());

            if (alreadyExistUser) {
                logger.warn("Tentativa de salvar usuário duplicado: {}", user);
                throw new DuplicatedRegisterException("Usuário já foi salvo no sistema.");
            }

            val savedUser = userRepository.save(user);
            logger.info("Usuário salvo com sucesso: login={}, id={}", user.getLogin(), user.getId());
            return savedUser;

        } catch (DuplicatedRegisterException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao salvar usuário: {}", e.getMessage());
            throw new GatewayException("Erro ao salvar usuário", e);
        }
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByLogin(String token) {
        return userRepository.findByLogin(token);
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void delete(String id) {
        try {
            userRepository.deleteById(id);
            logger.info("Usuário com ID {} excluído com sucesso.", id);
        } catch (Exception e) {
            logger.error("Erro ao excluir usuário: {}", e.getMessage());
            throw new GatewayException("Erro ao excluir usuário", e);
        }
    }
}
