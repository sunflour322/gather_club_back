package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.CurrencyTransactionResponse;

import java.util.List;

public interface UserCurrencyService {
    /**
     * Получить текущий баланс пользователя
     */
    Integer getUserBalance(Integer userId);
    
    /**
     * Добавить валюту пользователю
     */
    Integer addCurrency(Integer userId, Integer amount, String description, String transactionType, Integer referenceId);
    
    /**
     * Списать валюту у пользователя
     */
    Integer deductCurrency(Integer userId, Integer amount, String description, String transactionType, Integer referenceId);
    
    /**
     * Получить историю транзакций пользователя
     */
    List<CurrencyTransactionResponse> getUserTransactions(Integer userId);
    
    /**
     * Проверить, была ли уже начислена валюта за конкретное событие
     */
    boolean hasTransactionForReference(Integer userId, String transactionType, Integer referenceId);
} 