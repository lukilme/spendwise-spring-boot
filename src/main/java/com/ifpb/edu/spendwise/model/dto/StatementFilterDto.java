package com.ifpb.edu.spendwise.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementFilterDto{
    @NotNull(message = "Account ID cannot be null")
    @Positive(message = "Account ID must be positive")
    private Long accountId;
    
    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    @PastOrPresent(message = "End date must be in the past or present")
    private LocalDate endDate;
    
    @Builder.Default
    private List<@Positive Long> categoryIds = List.of();
    
    private CategoryTypes transactionType;
    private TransactionStatus transactionStatus;
    
    @Size(max = 100, message = "Search term cannot exceed 100 characters")
    private String searchTerm;
    
    @Builder.Default
    @DecimalMin(value = "0.0", message = "Minimum value cannot be negative")
    private BigDecimal minValue = BigDecimal.ZERO;
    
    @Builder.Default
    @DecimalMin(value = "0.0", message = "Maximum value cannot be negative")
    private BigDecimal maxValue = BigDecimal.valueOf(Double.MAX_VALUE);

    @AssertTrue(message = "End date must be after or equal to start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "Max value must be greater than min value")
    public boolean isValueRangeValid() {
        return maxValue.compareTo(minValue) >= 0;
    }
}
