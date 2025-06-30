package com.example.materabank.infra.repository;

import com.example.materabank.core.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUserId(String userId);
    Optional<Account> findByUserId(String userId);
}

