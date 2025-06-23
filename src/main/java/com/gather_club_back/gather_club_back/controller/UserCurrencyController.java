package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.CurrencyTransactionResponse;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/currency")
@RequiredArgsConstructor
@Tag(name = "Валюта пользователя", description = "API для управления валютой пользователя")
public class UserCurrencyController {

    private final UserCurrencyService userCurrencyService;

    @Operation(summary = "Получить баланс пользователя", description = "Возвращает текущий баланс валюты пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Баланс успешно получен", 
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Integer>> getUserBalance(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        Integer balance = userCurrencyService.getUserBalance(userId);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @Operation(summary = "Получить транзакции пользователя", description = "Возвращает список всех транзакций валюты пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список транзакций успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CurrencyTransactionResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<CurrencyTransactionResponse>> getUserTransactions(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        List<CurrencyTransactionResponse> transactions = userCurrencyService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
}
