package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.CurrencyTransaction;
import com.gather_club_back.gather_club_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, Integer> {
    List<CurrencyTransaction> findByUserOrderByCreatedAtDesc(User user);
    List<CurrencyTransaction> findByUserUserIdOrderByCreatedAtDesc(Integer userId);
    boolean existsByUserUserIdAndTransactionTypeAndReferenceId(Integer userId, String transactionType, Integer referenceId);
} 