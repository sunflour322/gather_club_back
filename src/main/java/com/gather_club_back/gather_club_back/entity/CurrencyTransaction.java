package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "currency_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CurrencyTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer amount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    private String description;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
