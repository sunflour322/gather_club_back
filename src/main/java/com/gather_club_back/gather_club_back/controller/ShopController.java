package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ShopItemActivationRequest;
import com.gather_club_back.gather_club_back.model.ShopItemRequest;
import com.gather_club_back.gather_club_back.model.ShopPurchaseRequest;
import com.gather_club_back.gather_club_back.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "Магазин", description = "API для управления магазином и покупками пользователей")
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "Получить все доступные товары", description = "Возвращает список всех доступных товаров в магазине для указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список товаров успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/items/{userId}")
    public ResponseEntity<Map<String, Object>> getAllShopItems(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        Map<String, Object> items = shopService.getAllShopItems(userId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Купить товар", description = "Покупает указанный товар для пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно куплен"),
        @ApiResponse(responseCode = "400", description = "Недостаточно средств или товар уже куплен"),
        @ApiResponse(responseCode = "404", description = "Пользователь или товар не найдены")
    })
    @PostMapping("/purchase/{userId}")
    public ResponseEntity<Object> purchaseItem(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные для покупки товара", required = true) @RequestBody ShopPurchaseRequest request) {
        Object purchasedItem = shopService.purchaseItem(userId, request);
        return ResponseEntity.ok(purchasedItem);
    }

    @Operation(summary = "Активировать/деактивировать товар", description = "Активирует или деактивирует купленный товар для пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус товара успешно изменен"),
        @ApiResponse(responseCode = "400", description = "Товар не куплен пользователем"),
        @ApiResponse(responseCode = "404", description = "Пользователь или товар не найдены")
    })
    @PostMapping("/activate/{userId}")
    public ResponseEntity<Object> setItemActive(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные для активации/деактивации товара", required = true) @RequestBody ShopItemActivationRequest request) {
        Object activatedItem = shopService.setItemActive(userId, request);
        return ResponseEntity.ok(activatedItem);
    }

    @Operation(summary = "Получить купленные товары", description = "Возвращает список всех товаров, купленных пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список купленных товаров успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/purchased/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPurchasedItems(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        Map<String, Object> purchasedItems = shopService.getUserPurchasedItems(userId);
        return ResponseEntity.ok(purchasedItems);
    }

    @Operation(summary = "Получить активные товары", description = "Возвращает список всех активных товаров пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список активных товаров успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/active/{userId}")
    public ResponseEntity<Map<String, Object>> getUserActiveItems(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        Map<String, Object> activeItems = shopService.getUserActiveItems(userId);
        return ResponseEntity.ok(activeItems);
    }
    
    @Operation(summary = "Создать новый товар", description = "Создает новый товар в магазине (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания товара")
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/admin/items/{itemType}")
    public ResponseEntity<Object> createShopItem(
            @Parameter(description = "Тип товара (frame, background, chat_theme)", required = true) @PathVariable String itemType,
            @Parameter(description = "Данные нового товара", required = true) @RequestBody ShopItemRequest request) {
        Object createdItem = shopService.createShopItem(
                itemType, 
                request.getName(), 
                request.getDescription(), 
                request.getPrice(), 
                request.getImageUrl());
        return ResponseEntity.ok(createdItem);
    }
    
    @Operation(summary = "Получить товар по ID", description = "Возвращает информацию о товаре по указанному ID (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для просмотра товара"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Object> getShopItemById(
            @Parameter(description = "Тип товара (frame, background, chat_theme)", required = true) @PathVariable String itemType,
            @Parameter(description = "ID товара", required = true) @PathVariable Integer itemId) {
        Object item = shopService.getShopItemById(itemType, itemId);
        return ResponseEntity.ok(item);
    }
    
    @Operation(summary = "Обновить товар", description = "Обновляет информацию о товаре по указанному ID (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные товара"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления товара"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @SecurityRequirement(name = "JWT")
    @PutMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Object> updateShopItem(
            @Parameter(description = "Тип товара (frame, background, chat_theme)", required = true) @PathVariable String itemType,
            @Parameter(description = "ID товара", required = true) @PathVariable Integer itemId,
            @Parameter(description = "Обновленные данные товара", required = true) @RequestBody ShopItemRequest request) {
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
    
    @Operation(summary = "Удалить товар", description = "Удаляет товар по указанному ID (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Товар успешно удален"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления товара"),
        @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/admin/items/{itemType}/{itemId}")
    public ResponseEntity<Boolean> deleteShopItem(
            @Parameter(description = "Тип товара (frame, background, chat_theme)", required = true) @PathVariable String itemType,
            @Parameter(description = "ID товара", required = true) @PathVariable Integer itemId) {
        boolean deleted = shopService.deleteShopItem(itemType, itemId);
        return ResponseEntity.ok(deleted);
    }
}
