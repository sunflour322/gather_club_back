package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "user_currency")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Integer currencyId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer balance = 0;

    @Column(name = "earned_total")
    private Integer earnedTotal = 0;

    @Column(name = "spent_total")
    private Integer spentTotal = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}