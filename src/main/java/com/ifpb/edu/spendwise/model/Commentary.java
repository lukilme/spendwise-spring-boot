package com.ifpb.edu.spendwise.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tb_commentary") 
@ToString(exclude = { "transaction" }) 
public class Commentary implements Serializable {

    private static final long serialVersionUID = 1L; // recomendado

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private LocalDate dateCreation;  

    private LocalDate dateLastUpdate;

    @ManyToOne
    private Transaction transaction;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDate.now();
        dateLastUpdate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateLastUpdate = LocalDate.now();
    }
}
