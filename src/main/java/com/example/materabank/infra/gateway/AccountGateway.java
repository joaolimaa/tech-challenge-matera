package com.example.materabank.infra.gateway;

import com.example.materabank.core.exception.DuplicatedRegisterException;
import com.example.materabank.core.exception.GatewayException;
import com.example.materabank.core.model.Account;
import com.example.materabank.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountGateway {
    private static final Logger logger = LogManager.getLogger(AccountGateway.class);
    private final AccountRepository accountRepository;

    public Account save(Account account) {
        try {
            val alreadyExists = accountRepository.existsByUserId(account.getUserId());
            if (alreadyExists) {
                logger.warn("Tentativa de criar conta duplicada para userId={}", account.getUserId());
                throw new DuplicatedRegisterException("Este usuário já possui uma conta cadastrada.");
            }

            val saved = accountRepository.save(account);
            logger.info("Conta criada com sucesso: id={}, userId={}", saved.getId(), saved.getUserId());
            return saved;
        } catch (DuplicatedRegisterException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao salvar conta: {}", e.getMessage(), e);
            throw new GatewayException("Erro ao salvar conta", e);
        }
    }

    public Account update(Account account) {
        try {
            val saved = accountRepository.save(account);
            logger.info("Conta atualizada com sucesso: id={}, userId={}, balance={}", saved.getId(), saved.getUserId(), saved.getBalance());
            return saved;

        } catch (Exception e) {
            logger.error("Erro ao atualizar conta: {}", e.getMessage(), e);
            throw new GatewayException("Erro ao atualizar conta", e);
        }
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByUserId(String userId) {
        return accountRepository.findByUserId(String.valueOf(userId));
    }

    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    public void delete(Long id) {
        try {
            accountRepository.deleteById(id);
            logger.info("Conta com ID {} excluída com sucesso.", id);
        } catch (Exception e) {
            logger.error("Erro ao excluir conta: {}", e.getMessage(), e);
            throw new GatewayException("Erro ao excluir conta", e);
        }
    }

}
