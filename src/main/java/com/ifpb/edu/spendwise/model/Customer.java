package com.ifpb.edu.spendwise.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.ifpb.edu.spendwise.model.enumerator.StatusCustomer;
import com.ifpb.edu.spendwise.model.enumerator.UserRoles;

@Entity
@Table(name = "tb_customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "password", "accounts" })
@Schema(description = "Entity that represents a customer of the system.")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the customer", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, min = 6, message = "Name must not exceed 100 characters and not be less than 6")
    @Schema(description = "Full name of the customer", example = "John Doe")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    @Schema(description = "User's email address", example = "john.doe@email.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Customer's password (encrypted). Not exposed in public.", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Builder.Default
    @Schema(description = "Indicates if the user is active", example = "true")
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_customer")
    @Schema(description = "User role", example = "COMMON")
    @Builder.Default
    private UserRoles role = UserRoles.ROLE_COMMON;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_customer")
    @Schema(description = "Customer status", example = "ACTIVE")
    @Builder.Default
    private StatusCustomer status = StatusCustomer.ACTIVE;

    @Schema(description = "Date and time of the last access", example = "2025-07-22T08:30:00")
    private LocalDateTime lastAccess;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "List of accounts associated with the customer", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Account> accounts;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Date and time of creation", example = "2025-07-22T08:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}
