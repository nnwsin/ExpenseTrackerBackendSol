package com.sol.expensetrackerfinal.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data // Getters, Setters, toString, equals, hashCode
@NoArgsConstructor // No-args constructor (JPA requirement)
@AllArgsConstructor // All-args constructor
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ExpenseGroup> createdGroups;

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private List<ExpenseGroup> groups;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Split> splits;
}
