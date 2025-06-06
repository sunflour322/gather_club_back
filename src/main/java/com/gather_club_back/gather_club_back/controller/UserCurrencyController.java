package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.CurrencyTransactionResponse;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/currency")
@RequiredArgsConstructor
public class UserCurrencyController {

    private final UserCurrencyService userCurrencyService;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Integer>> getUserBalance(@PathVariable Integer userId) {
        Integer balance = userCurrencyService.getUserBalance(userId);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<CurrencyTransactionResponse>> getUserTransactions(@PathVariable Integer userId) {
        List<CurrencyTransactionResponse> transactions = userCurrencyService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
} 