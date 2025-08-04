package com.ifpb.edu.spendwise.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tb_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "transactions")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Account name is required")
    @Size(max = 50, message = "Account name must not exceed 50 characters")
    private String name;
    
    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "account_type")
    private AccountTypes accountType;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @NotNull
    private Customer customer;
    
    @Future(message = "Expiration date must be in the future")
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @NotNull
    @Builder.Default
    @Column(name="is_active", nullable = false)
    private boolean isActive = true;
    
    public boolean addTransaction(Transaction transaction) {
        if (transaction != null && !this.transactions.contains(transaction)) {
            this.transactions.add(transaction);
            if (transaction.getAccount() != this) {
                transaction.setAccount(this);
            }
            return true;
        }
        return false;
    }
    
    public Boolean removeTransaction(Transaction transaction) {
        if (transaction == null) return false;
        
        boolean isRemoved = this.transactions.remove(transaction);
        if (isRemoved) {
            transaction.setAccount(null);
        }
        return isRemoved;
    }
    
    public void updateBalance(BigDecimal amount) {
        if (amount != null) {
            this.balance = this.balance.add(amount);
        }
    }
}