package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.ChatTheme;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserChatTheme;
import com.gather_club_back.gather_club_back.mapper.ChatThemeMapper;
import com.gather_club_back.gather_club_back.model.ChatThemeResponse;
import com.gather_club_back.gather_club_back.repository.ChatThemeRepository;
import com.gather_club_back.gather_club_back.repository.UserChatThemeRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.ChatThemeService;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatThemeServiceImpl implements ChatThemeService {

    private final ChatThemeRepository chatThemeRepository;
    private final UserChatThemeRepository userChatThemeRepository;
    private final UserRepository userRepository;
    private final UserCurrencyService currencyService;
    private final ChatThemeMapper chatThemeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChatThemeResponse> getAllThemes(Integer userId) {
        List<ChatTheme> themes = chatThemeRepository.findByIsActiveTrue();
        List<UserChatTheme> userThemes = userChatThemeRepository.findByUserUserId(userId);

        return themes.stream()
                .map(theme -> {
                    boolean isPurchased = userThemes.stream()
                            .anyMatch(userTheme -> userTheme.getTheme().getThemeId().equals(theme.getThemeId()));
                    
                    boolean isActive = isPurchased && userThemes.stream()
                            .anyMatch(userTheme -> userTheme.getTheme().getThemeId().equals(theme.getThemeId()) 
                                    && userTheme.getIsActive());
                    
                    return chatThemeMapper.toResponse(theme, isPurchased, isActive);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatThemeResponse getThemeById(Integer themeId, Integer userId) {
        ChatTheme theme = chatThemeRepository.findById(themeId)
                .orElseThrow(() -> new EntityNotFoundException("Тема чата не найдена"));
        
        boolean isPurchased = userChatThemeRepository.existsByUserUserIdAndThemeThemeId(userId, themeId);
        boolean isActive = false;
        
        if (isPurchased) {
            UserChatTheme userTheme = userChatThemeRepository.findByUserUserIdAndThemeThemeId(userId, themeId)
                    .orElseThrow(() -> new EntityNotFoundException("Тема чата пользователя не найдена"));
            isActive = userTheme.getIsActive();
        }
        
        return chatThemeMapper.toResponse(theme, isPurchased, isActive);
    }

    @Override
    @Transactional
    public ChatThemeResponse purchaseTheme(Integer themeId, Integer userId) {
        ChatTheme theme = chatThemeRepository.findById(themeId)
                .orElseThrow(() -> new EntityNotFoundException("Тема чата не найдена"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        if (!theme.getIsActive()) {
            throw new IllegalStateException("Тема чата недоступна для покупки");
        }
        
        if (userChatThemeRepository.existsByUserUserIdAndThemeThemeId(userId, themeId)) {
            throw new IllegalStateException("Тема чата уже куплена");
        }
        
        // Списываем валюту
        currencyService.deductCurrency(
                userId, 
                theme.getPrice(), 
                "Покупка темы чата: " + theme.getName(), 
                "purchase_chat_theme", 
                themeId
        );
        
        // Создаем запись о покупке
        UserChatTheme userChatTheme = new UserChatTheme();
        userChatTheme.setUser(user);
        userChatTheme.setTheme(theme);
        userChatTheme.setPurchasedAt(LocalDateTime.now());
        userChatTheme.setIsActive(false);
        
        userChatThemeRepository.save(userChatTheme);
        
        return chatThemeMapper.toResponse(theme, true, false);
    }

    @Override
    @Transactional
    public ChatThemeResponse setThemeActive(Integer themeId, Integer userId, Boolean active) {
        UserChatTheme userChatTheme = userChatThemeRepository.findByUserUserIdAndThemeThemeId(userId, themeId)
                .orElseThrow(() -> new EntityNotFoundException("Тема чата пользователя не найдена"));
        
        if (active) {
            // Деактивируем все темы пользователя
            userChatThemeRepository.deactivateAllUserThemes(userId);
            
            // Активируем выбранную тему
            userChatTheme.setIsActive(true);
        } else {
            userChatTheme.setIsActive(false);
        }
        
        userChatThemeRepository.save(userChatTheme);
        
        return chatThemeMapper.toResponse(userChatTheme);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatThemeResponse getCurrentActiveTheme(Integer userId) {
        return userChatThemeRepository.findByUserUserIdAndIsActiveTrue(userId)
                .map(chatThemeMapper::toResponse)
                .orElse(null);
    }
}
