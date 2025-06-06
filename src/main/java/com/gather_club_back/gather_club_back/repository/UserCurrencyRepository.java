package com.gather_club_back.gather_club_back.repository;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCurrencyRepository extends JpaRepository<UserCurrency, Integer> {
    Optional<UserCurrency> findByUser(User user);
    Optional<UserCurrency> findByUserUserId(Integer userId);
} 