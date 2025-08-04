package com.ifpb.edu.spendwise.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.ifpb.edu.spendwise.model.enumerator.AccountTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilterDTO {
    private List<AccountTypes> accountTypes;
    private String sortOrder = "asc";
    private String sortBy = "expirationDate";
    private String searchTerm;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Boolean activeOnly = true;
}