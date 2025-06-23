package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;

import java.util.List;

public interface ProfileBackgroundService {
    
    /**
     * Получить список всех доступных фоновых изображений профиля
     */
    List<ProfileBackgroundResponse> getAllBackgrounds(Integer userId);
    
    /**
     * Получить фоновое изображение профиля по ID
     */
    ProfileBackgroundResponse getBackgroundById(Integer backgroundId, Integer userId);
    
    /**
     * Купить фоновое изображение профиля
     */
    ProfileBackgroundResponse purchaseBackground(Integer backgroundId, Integer userId);
    
    /**
     * Активировать/деактивировать фоновое изображение профиля
     */
    ProfileBackgroundResponse setBackgroundActive(Integer backgroundId, Integer userId, Boolean active);
    
    /**
     * Получить текущее активное фоновое изображение профиля пользователя
     */
    ProfileBackgroundResponse getCurrentActiveBackground(Integer userId);
}
