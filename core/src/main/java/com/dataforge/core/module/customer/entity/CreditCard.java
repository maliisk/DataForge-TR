package com.dataforge.core.module.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String cardNumber;

    @Column(nullable = false)
    private BigDecimal cardLimit;

    @Column(nullable = false)
    private BigDecimal currentDebt;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false, length = 3)
    private String cvv;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}