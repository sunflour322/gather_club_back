package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import com.gather_club_back.gather_club_back.model.ShopItemActivationRequest;
import com.gather_club_back.gather_club_back.model.ShopPurchaseRequest;

import java.util.List;
import java.util.Map;

public interface ShopService {
    
    /**
     * Получить все доступные товары в магазине для пользователя
     */
    Map<String, Object> getAllShopItems(Integer userId);
    
    /**
     * Купить товар в магазине
     */
    Object purchaseItem(Integer userId, ShopPurchaseRequest request);
    
    /**
     * Активировать/деактивировать купленный товар
     */
    Object setItemActive(Integer userId, ShopItemActivationRequest request);
    
    /**
     * Получить список купленных пользователем товаров
     */
    Map<String, Object> getUserPurchasedItems(Integer userId);
    
    /**
     * Получить список активных товаров пользователя
     */
    Map<String, Object> getUserActiveItems(Integer userId);
    
    /**
     * Создать новый товар в магазине
     * @param itemType тип товара ("profile_frame", "profile_background", "chat_theme")
     * @param name название товара
     * @param description описание товара
     * @param price цена товара
     * @param imageUrl URL изображения товара
     * @return созданный товар
     */
    Object createShopItem(String itemType, String name, String description, Integer price, String imageUrl);
    
    /**
     * Получить товар по ID
     * @param itemType тип товара ("profile_frame", "profile_background", "chat_theme")
     * @param itemId ID товара
     * @return товар
     */
    Object getShopItemById(String itemType, Integer itemId);
    
    /**
     * Обновить товар
     * @param itemType тип товара ("profile_frame", "profile_background", "chat_theme")
     * @param itemId ID товара
     * @param name новое название товара (null если не меняется)
     * @param description новое описание товара (null если не меняется)
     * @param price новая цена товара (null если не меняется)
     * @param imageUrl новый URL изображения товара (null если не меняется)
     * @param isActive статус активности товара (null если не меняется)
     * @return обновленный товар
     */
    Object updateShopItem(String itemType, Integer itemId, String name, String description, 
                         Integer price, String imageUrl, Boolean isActive);
    
    /**
     * Удалить товар
     * @param itemType тип товара ("profile_frame", "profile_background", "chat_theme")
     * @param itemId ID товара
     * @return true если товар успешно удален
     */
    boolean deleteShopItem(String itemType, Integer itemId);
}
