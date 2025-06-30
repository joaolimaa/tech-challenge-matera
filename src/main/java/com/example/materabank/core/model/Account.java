package com.example.materabank.core.model;

import com.example.materabank.core.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id"}) })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private BigDecimal balance;

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            String formattedAmount = formatter.format(amount);
            throw new InsufficientBalanceException("Saldo insuficiente para débito de " + formattedAmount);
        }
        this.balance = this.balance.subtract(amount);
    }
}
