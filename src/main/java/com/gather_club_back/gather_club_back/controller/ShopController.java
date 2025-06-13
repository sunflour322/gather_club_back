package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ShopItemActivationRequest;
import com.gather_club_back.gather_club_back.model.ShopItemRequest;
import com.gather_club_back.gather_club_back.model.ShopPurchaseRequest;
import com.gather_club_back.gather_club_back.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    /**
     * Получить все доступные товары в магазине
     */
    @GetMapping("/items/{userId}")
    public ResponseEntity<Map<String, Object>> getAllShopItems(@PathVariable Integer userId) {
        Map<String, Object> items = shopService.getAllShopItems(userId);
        return ResponseEntity.ok(items);
    }

    /**
     * Купить товар в магазине
     */
    @PostMapping("/purchase/{userId}")
    public ResponseEntity<Object> purchaseItem(
            @PathVariable Integer userId,
            @RequestBody ShopPurchaseRequest request) {
        Object purchasedItem = shopService.purchaseItem(userId, request);
        return ResponseEntity.ok(purchasedItem);
    }

    /**
     * Активировать/деактивировать купленный товар
     */
    @PostMapping("/activate/{userId}")
    public ResponseEntity<Object> setItemActive(
            @PathVariable Integer userId,
            @RequestBody ShopItemActivationRequest request) {
        Object activatedItem = shopService.setItemActive(userId, request);
        return ResponseEntity.ok(activatedItem);
    }

    /**
     * Получить список купленных пользователем товаров
     */
    @GetMapping("/purchased/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPurchasedItems(@PathVariable Integer userId) {
        Map<String, Object> purchasedItems = shopService.getUserPurchasedItems(userId);
        return ResponseEntity.ok(purchasedItems);
    }

    /**
     * Получить список активных товаров пользователя
     */
    @GetMapping("/active/{userId}")
    public ResponseEntity<Map<String, Object>> getUserActiveItems(@PathVariable Integer userId) {
        Map<String, Object> activeItems = shopService.getUserActiveItems(userId);
        return ResponseEntity.ok(activeItems);
    }
    
    /**
     * Создать новый товар в магазине
     */
    @PostMapping("/admin/items/{itemType}")
    public ResponseEntity<Object> createShopItem(
            @PathVariable String itemType,
            @RequestBody ShopItemRequest request) {
        Object createdItem = shopService.createShopItem(
                itemType, 
                request.getName(), 
                request.getDescription(), 
                request.getPrice(), 
                request.getImageUrl());
        return ResponseEntity.ok(createdItem);
    }
    
    /**
     * Получить товар по ID
     */
    @GetMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Object> getShopItemById(
            @PathVariable String itemType,
            @PathVariable Integer itemId) {
        Object item = shopService.getShopItemById(itemType, itemId);
        return ResponseEntity.ok(item);
    }
    
    /**
     * Обновить товар
     */
    @PutMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Object> updateShopItem(
            @PathVariable String itemType,
            @PathVariable Integer itemId,
            @RequestBody ShopItemRequest request) {
        Object updatedItem = shopService.updateShopItem(
                itemType, 
                itemId, 
                request.getName(), 
                request.getDescription(), 
                request.getPrice(), 
                request.getImageUrl(), 
                request.getIsActive());
        return ResponseEntity.ok(updatedItem);
    }
    
    /**
     * Удалить товар
     */
    @DeleteMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Boolean> deleteShopItem(
            @PathVariable String itemType,
            @PathVariable Integer itemId) {
        boolean deleted = shopService.deleteShopItem(itemType, itemId);
        return ResponseEntity.ok(deleted);
    }
}
