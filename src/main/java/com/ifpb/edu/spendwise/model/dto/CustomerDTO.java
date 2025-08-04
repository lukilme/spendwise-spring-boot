package com.ifpb.edu.spendwise.model.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private boolean active;
    private String phoneNumber;
    private Integer totalAccounts;
}
