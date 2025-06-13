package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.ChatTheme;
import com.gather_club_back.gather_club_back.entity.ProfileBackground;
import com.gather_club_back.gather_club_back.entity.ProfileFrame;
import com.gather_club_back.gather_club_back.model.ChatThemeResponse;
import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import com.gather_club_back.gather_club_back.model.ShopItemActivationRequest;
import com.gather_club_back.gather_club_back.model.ShopPurchaseRequest;
import com.gather_club_back.gather_club_back.repository.ChatThemeRepository;
import com.gather_club_back.gather_club_back.repository.ProfileBackgroundRepository;
import com.gather_club_back.gather_club_back.repository.ProfileFrameRepository;
import com.gather_club_back.gather_club_back.service.ChatThemeService;
import com.gather_club_back.gather_club_back.service.ProfileBackgroundService;
import com.gather_club_back.gather_club_back.service.ProfileFrameService;
import com.gather_club_back.gather_club_back.service.ShopService;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ProfileFrameService frameService;
    private final ProfileBackgroundService backgroundService;
    private final ChatThemeService chatThemeService;
    private final UserCurrencyService currencyService;
    private final ProfileFrameRepository frameRepository;
    private final ProfileBackgroundRepository backgroundRepository;
    private final ChatThemeRepository chatThemeRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllShopItems(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        // Получаем все рамки профиля
        List<ProfileFrameResponse> frames = frameService.getAllFrames(userId);
        result.put("profileFrames", frames);
        
        // Получаем все фоны профиля
        List<ProfileBackgroundResponse> backgrounds = backgroundService.getAllBackgrounds(userId);
        result.put("profileBackgrounds", backgrounds);
        
        // Получаем все темы чатов
        List<ChatThemeResponse> chatThemes = chatThemeService.getAllThemes(userId);
        result.put("chatThemes", chatThemes);
        
        // Получаем текущий баланс пользователя
        Integer balance = currencyService.getUserBalance(userId);
        result.put("userBalance", balance);
        
        return result;
    }

    @Override
    @Transactional
    public Object purchaseItem(Integer userId, ShopPurchaseRequest request) {
        String itemType = request.getItemType();
        Integer itemId = request.getItemId();
        
        switch (itemType) {
            case "profile_frame":
                return frameService.purchaseFrame(itemId, userId);
                
            case "profile_background":
                return backgroundService.purchaseBackground(itemId, userId);
                
            case "chat_theme":
                return chatThemeService.purchaseTheme(itemId, userId);
                
            default:
                throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
        }
    }

    @Override
    @Transactional
    public Object setItemActive(Integer userId, ShopItemActivationRequest request) {
        String itemType = request.getItemType();
        Integer itemId = request.getItemId();
        Boolean activate = request.getActivate();
        
        switch (itemType) {
            case "profile_frame":
                return frameService.setFrameActive(itemId, userId, activate);
                
            case "profile_background":
                return backgroundService.setBackgroundActive(itemId, userId, activate);
                
            case "chat_theme":
                return chatThemeService.setThemeActive(itemId, userId, activate);
                
            default:
                throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserPurchasedItems(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        // Получаем все купленные рамки профиля
        List<ProfileFrameResponse> frames = frameService.getAllFrames(userId).stream()
                .filter(ProfileFrameResponse::getIsPurchased)
                .toList();
        result.put("profileFrames", frames);
        
        // Получаем все купленные фоны профиля
        List<ProfileBackgroundResponse> backgrounds = backgroundService.getAllBackgrounds(userId).stream()
                .filter(ProfileBackgroundResponse::getIsPurchased)
                .toList();
        result.put("profileBackgrounds", backgrounds);
        
        // Получаем все купленные темы чатов
        List<ChatThemeResponse> chatThemes = chatThemeService.getAllThemes(userId).stream()
                .filter(ChatThemeResponse::getIsPurchased)
                .toList();
        result.put("chatThemes", chatThemes);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserActiveItems(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        // Получаем активную рамку профиля
        ProfileFrameResponse activeFrame = frameService.getCurrentActiveFrame(userId);
        result.put("activeProfileFrame", activeFrame);
        
        // Получаем активный фон профиля
        ProfileBackgroundResponse activeBackground = backgroundService.getCurrentActiveBackground(userId);
        result.put("activeProfileBackground", activeBackground);
        
        // Получаем активную тему чата
        ChatThemeResponse activeChatTheme = chatThemeService.getCurrentActiveTheme(userId);
        result.put("activeChatTheme", activeChatTheme);
        
        return result;
    }
    
    @Override
    @Transactional
    public Object createShopItem(String itemType, String name, String description, Integer price, String imageUrl) {
        switch (itemType) {
            case "profile_frame":
                ProfileFrame frame = new ProfileFrame();
                frame.setName(name);
                frame.setDescription(description);
                frame.setPrice(price);
                frame.setPreviewUrl(imageUrl);
                frame.setIsActive(true);
                return frameRepository.save(frame);
                
            case "profile_background":
                ProfileBackground background = new ProfileBackground();
                background.setName(name);
                background.setDescription(description);
                background.setPrice(price);
                background.setPreviewUrl(imageUrl);
                background.setIsActive(true);
                return backgroundRepository.save(background);
                
            case "chat_theme":
                ChatTheme chatTheme = new ChatTheme();
                chatTheme.setName(name);
                chatTheme.setDescription(description);
                chatTheme.setPrice(price);
                chatTheme.setPreviewUrl(imageUrl);
                chatTheme.setIsActive(true);
                return chatThemeRepository.save(chatTheme);
                
            default:
                throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getShopItemById(String itemType, Integer itemId) {
        switch (itemType) {
            case "profile_frame":
                return frameRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Рамка профиля с ID " + itemId + " не найдена"));
                
            case "profile_background":
                return backgroundRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Фон профиля с ID " + itemId + " не найден"));
                
            case "chat_theme":
                return chatThemeRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Тема чата с ID " + itemId + " не найдена"));
                
            default:
                throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
        }
    }
    
    @Override
    @Transactional
    public Object updateShopItem(String itemType, Integer itemId, String name, String description, 
                               Integer price, String imageUrl, Boolean isActive) {
        switch (itemType) {
            case "profile_frame":
                ProfileFrame frame = frameRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Рамка профиля с ID " + itemId + " не найдена"));
                
                if (name != null) frame.setName(name);
                if (description != null) frame.setDescription(description);
                if (price != null) frame.setPrice(price);
                if (imageUrl != null) frame.setPreviewUrl(imageUrl);
                if (isActive != null) frame.setIsActive(isActive);
                
                return frameRepository.save(frame);
                
            case "profile_background":
                ProfileBackground background = backgroundRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Фон профиля с ID " + itemId + " не найден"));
                
                if (name != null) background.setName(name);
                if (description != null) background.setDescription(description);
                if (price != null) background.setPrice(price);
                if (imageUrl != null) background.setPreviewUrl(imageUrl);
                if (isActive != null) background.setIsActive(isActive);
                
                return backgroundRepository.save(background);
                
            case "chat_theme":
                ChatTheme chatTheme = chatThemeRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Тема чата с ID " + itemId + " не найдена"));
                
                if (name != null) chatTheme.setName(name);
                if (description != null) chatTheme.setDescription(description);
                if (price != null) chatTheme.setPrice(price);
                if (imageUrl != null) chatTheme.setPreviewUrl(imageUrl);
                if (isActive != null) chatTheme.setIsActive(isActive);
                
                return chatThemeRepository.save(chatTheme);
                
            default:
                throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteShopItem(String itemType, Integer itemId) {
        try {
            switch (itemType) {
                case "profile_frame":
                    ProfileFrame frame = frameRepository.findById(itemId)
                            .orElseThrow(() -> new EntityNotFoundException("Рамка профиля с ID " + itemId + " не найдена"));
                    
                    // Логическое удаление (деактивация)
                    frame.setIsActive(false);
                    frameRepository.save(frame);
                    return true;
                    
                case "profile_background":
                    ProfileBackground background = backgroundRepository.findById(itemId)
                            .orElseThrow(() -> new EntityNotFoundException("Фон профиля с ID " + itemId + " не найден"));
                    
                    // Логическое удаление (деактивация)
                    background.setIsActive(false);
                    backgroundRepository.save(background);
                    return true;
                    
                case "chat_theme":
                    ChatTheme chatTheme = chatThemeRepository.findById(itemId)
                            .orElseThrow(() -> new EntityNotFoundException("Тема чата с ID " + itemId + " не найдена"));
                    
                    // Логическое удаление (деактивация)
                    chatTheme.setIsActive(false);
                    chatThemeRepository.save(chatTheme);
                    return true;
                    
                default:
                    throw new IllegalArgumentException("Неизвестный тип товара: " + itemType);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении товара типа {} с ID {}: {}", itemType, itemId, e.getMessage());
            return false;
        }
    }
}
