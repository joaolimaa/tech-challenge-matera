package com.example.materabank.application;

import com.example.materabank.core.exception.InsufficientBalanceException;
import com.example.materabank.core.exception.NotFoundException;
import com.example.materabank.core.model.Account;
import com.example.materabank.core.model.enums.TransactionType;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.request.TransactionRequestDTO;
import com.example.materabank.infra.controller.dto.response.AccountResponseDTO;
import com.example.materabank.infra.gateway.AccountGateway;
import com.example.materabank.infra.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.materabank.infra.mapper.AccountMapper.toResponseDTO;

@Service
@RequiredArgsConstructor
public class AccountUseCase {
    private final AccountGateway accountGateway;
    private final Map<Long, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    public AccountResponseDTO create(AccountRequestDTO dto) {
        val account = Account.builder()
                .userId(dto.userId())
                .balance(BigDecimal.ZERO)
                .build();
        val savedAccount = accountGateway.save(account);
        return toResponseDTO(savedAccount);
    }

    public List<AccountResponseDTO> listAll() {
        return accountGateway.listAll()
                .stream()
                .map(AccountMapper::toResponseDTO)
                .toList();
    }

    public AccountResponseDTO findById(Long id) {
        val account = accountGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada para o id: " + id));
        return toResponseDTO(account);
    }

    public void delete(Long id) {
        if (accountGateway.findById(id).isEmpty()) {
            throw new NotFoundException("Conta não encontrada para o id: " + id);
        }
        accountGateway.delete(id);
    }

    public void processTransactions(Long accountId, List<TransactionRequestDTO> transactions) {
        val lock = getLockForAccount(accountId);
        lock.lock();
        try {
            val account = accountGateway.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Conta não encontrada para o id: " + accountId));

            for (TransactionRequestDTO transaction : transactions) {
                if (transaction.type() == TransactionType.CREDIT) {
                    account.credit(transaction.amount());
                } else if (transaction.type() == TransactionType.DEBIT) {
                    try {
                        account.debit(transaction.amount());
                    } catch (IllegalArgumentException e) {
                        throw new InsufficientBalanceException(e.getMessage());
                    }
                }
            }

            accountGateway.update(account);
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLockForAccount(Long accountId) {
        return accountLocks.computeIfAbsent(accountId, id -> new ReentrantLock());
    }
}
