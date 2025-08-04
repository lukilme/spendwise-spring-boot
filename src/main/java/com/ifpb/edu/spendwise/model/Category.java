package com.ifpb.edu.spendwise.model;

import com.ifpb.edu.spendwise.model.enumerator.CategoryTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    @Column(unique = true)
    private String name;
    
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "category_type")
    private CategoryTypes categoryType;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean active = true;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
