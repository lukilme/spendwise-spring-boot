package com.ifpb.edu.spendwise.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tb_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "account")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;
    
    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;


    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Commentary> commentaries = new ArrayList<>();
    

    public void setAccount(Account account) {
        this.account = account;
        if (account != null && !account.getTransactions().contains(this)) {
            account.addTransaction(this);
        }
    }

    public void addCommentary(Commentary commentary) {
        commentaries.add(commentary);
        commentary.setTransaction(this);
    }

    public void removeCommentary(Commentary commentary) {
        commentaries.remove(commentary);
        commentary.setTransaction(null);
    }
}