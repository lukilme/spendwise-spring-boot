package com.ifpb.edu.spendwise.model.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Value must not be null")
    @DecimalMin(value = "0.01", message = "Value must be greater than 0")
    private BigDecimal value;

    @NotNull(message = "Transaction date must not be null")
    private LocalDateTime transactionDate;

    @NotNull(message = "Category must not be null")
    private Long categoryId;

    @NotNull(message="seila")
    private String categoryName = "Teaste";

    @NotNull(message = "Account must not be null")
    private Long accountId;

    private Long commentCount;

}

