package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.ChatThemeResponse;

import java.util.List;

public interface ChatThemeService {
    
    /**
     * Получить список всех доступных тем чата
     */
    List<ChatThemeResponse> getAllThemes(Integer userId);
    
    /**
     * Получить тему чата по ID
     */
    ChatThemeResponse getThemeById(Integer themeId, Integer userId);
    
    /**
     * Купить тему чата
     */
    ChatThemeResponse purchaseTheme(Integer themeId, Integer userId);
    
    /**
     * Активировать/деактивировать тему чата
     */
    ChatThemeResponse setThemeActive(Integer themeId, Integer userId, Boolean active);
    
    /**
     * Получить текущую активную тему чата пользователя
     */
    ChatThemeResponse getCurrentActiveTheme(Integer userId);
}
