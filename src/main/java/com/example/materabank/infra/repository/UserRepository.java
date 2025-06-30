package com.example.materabank.infra.repository;

import com.example.materabank.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByLoginIgnoreCase(String login);
    Optional<User> findByLogin(String login);
}
