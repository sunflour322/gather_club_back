package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;

import java.util.List;

public interface ProfileFrameService {
    
    /**
     * Получить список всех доступных рамок профиля
     */
    List<ProfileFrameResponse> getAllFrames(Integer userId);
    
    /**
     * Получить рамку профиля по ID
     */
    ProfileFrameResponse getFrameById(Integer frameId, Integer userId);
    
    /**
     * Купить рамку профиля
     */
    ProfileFrameResponse purchaseFrame(Integer frameId, Integer userId);
    
    /**
     * Активировать/деактивировать рамку профиля
     */
    ProfileFrameResponse setFrameActive(Integer frameId, Integer userId, Boolean active);
    
    /**
     * Получить текущую активную рамку профиля пользователя
     */
    ProfileFrameResponse getCurrentActiveFrame(Integer userId);
}
