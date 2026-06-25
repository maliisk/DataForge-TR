package com.dataforge.core.module.customer.entity;

import com.dataforge.core.module.account.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Column(unique = true, length = 11)
    private String tckn;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    private LocalDate birthDate;

    @Column(unique = true, length = 10)
    private String taxNumber;

    @Column(length = 100)
    private String companyName;

    @Column(length = 50)
    private String sector;

    @Column(nullable = false)
    private Integer riskScore;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CreditCard> creditCards = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Loan> loans = new ArrayList<>();
}