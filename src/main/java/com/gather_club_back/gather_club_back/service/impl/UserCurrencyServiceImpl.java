package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.CurrencyTransaction;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserCurrency;
import com.gather_club_back.gather_club_back.mapper.CurrencyTransactionMapper;
import com.gather_club_back.gather_club_back.model.CurrencyTransactionResponse;
import com.gather_club_back.gather_club_back.repository.CurrencyTransactionRepository;
import com.gather_club_back.gather_club_back.repository.UserCurrencyRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCurrencyServiceImpl implements UserCurrencyService {

    private final UserCurrencyRepository userCurrencyRepository;
    private final CurrencyTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrencyTransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public Integer getUserBalance(Integer userId) {
        return userCurrencyRepository.findByUserUserId(userId)
                .map(UserCurrency::getBalance)
                .orElse(0);
    }

    @Override
    @Transactional
    public Integer addCurrency(Integer userId, Integer amount, String description, String transactionType, Integer referenceId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        UserCurrency userCurrency = userCurrencyRepository.findByUser(user)
                .orElseGet(() -> {
                    UserCurrency newCurrency = new UserCurrency()
                            .setUser(user)
                            .setBalance(0)
                            .setEarnedTotal(0)
                            .setSpentTotal(0)
                            .setLastUpdated(LocalDateTime.now());
                    return userCurrencyRepository.save(newCurrency);
                });
        
        // Обновляем баланс и общую сумму заработанного
        userCurrency.setBalance(userCurrency.getBalance() + amount);
        userCurrency.setEarnedTotal(userCurrency.getEarnedTotal() + amount);
        userCurrency.setLastUpdated(LocalDateTime.now());
        userCurrencyRepository.save(userCurrency);
        
        // Создаем транзакцию
        CurrencyTransaction transaction = new CurrencyTransaction()
                .setUser(user)
                .setAmount(amount)
                .setTransactionType(transactionType)
                .setDescription(description)
                .setReferenceId(referenceId)
                .setCreatedAt(LocalDateTime.now());
        
        transactionRepository.save(transaction);
        
        log.info("Добавлено {} валюты пользователю с ID {}, тип: {}, новый баланс: {}", 
                amount, userId, transactionType, userCurrency.getBalance());
        
        return userCurrency.getBalance();
    }

    @Override
    @Transactional
    public Integer deductCurrency(Integer userId, Integer amount, String description, String transactionType, Integer referenceId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        UserCurrency userCurrency = userCurrencyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("У пользователя нет счета с валютой"));
        
        if (userCurrency.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств");
        }
        
        // Обновляем баланс и общую сумму потраченного
        userCurrency.setBalance(userCurrency.getBalance() - amount);
        userCurrency.setSpentTotal(userCurrency.getSpentTotal() + amount);
        userCurrency.setLastUpdated(LocalDateTime.now());
        userCurrencyRepository.save(userCurrency);
        
        // Создаем транзакцию (отрицательная сумма для списания)
        CurrencyTransaction transaction = new CurrencyTransaction()
                .setUser(user)
                .setAmount(-amount)
                .setTransactionType(transactionType)
                .setDescription(description)
                .setReferenceId(referenceId)
                .setCreatedAt(LocalDateTime.now());
        
        transactionRepository.save(transaction);
        
        log.info("Списано {} валюты у пользователя с ID {}, тип: {}, новый баланс: {}", 
                amount, userId, transactionType, userCurrency.getBalance());
        
        return userCurrency.getBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyTransactionResponse> getUserTransactions(Integer userId) {
        return transactionRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(transactionMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasTransactionForReference(Integer userId, String transactionType, Integer referenceId) {
        return transactionRepository.existsByUserUserIdAndTransactionTypeAndReferenceId(userId, transactionType, referenceId);
    }
} 