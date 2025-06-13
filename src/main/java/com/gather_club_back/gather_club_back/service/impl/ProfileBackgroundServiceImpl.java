package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.ProfileBackground;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserProfileBackground;
import com.gather_club_back.gather_club_back.mapper.ProfileBackgroundMapper;
import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import com.gather_club_back.gather_club_back.repository.ProfileBackgroundRepository;
import com.gather_club_back.gather_club_back.repository.UserProfileBackgroundRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.ProfileBackgroundService;
import com.gather_club_back.gather_club_back.service.UserCurrencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileBackgroundServiceImpl implements ProfileBackgroundService {

    private final ProfileBackgroundRepository backgroundRepository;
    private final UserProfileBackgroundRepository userBackgroundRepository;
    private final UserRepository userRepository;
    private final ProfileBackgroundMapper backgroundMapper;
    private final UserCurrencyService currencyService;

    @Override
    @Transactional(readOnly = true)
    public List<ProfileBackgroundResponse> getAllBackgrounds(Integer userId) {
        List<ProfileBackground> backgrounds = backgroundRepository.findByIsActiveTrue();
        List<UserProfileBackground> userBackgrounds = userBackgroundRepository.findByUserUserId(userId);
        
        return backgroundMapper.toModelList(backgrounds, userBackgrounds);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileBackgroundResponse getBackgroundById(Integer backgroundId, Integer userId) {
        ProfileBackground background = backgroundRepository.findById(backgroundId)
                .orElseThrow(() -> new EntityNotFoundException("Фон профиля не найден"));
        
        boolean isPurchased = userBackgroundRepository.existsByUserUserIdAndBackgroundBackgroundId(userId, backgroundId);
        boolean isActive = false;
        
        if (isPurchased) {
            UserProfileBackground userBackground = userBackgroundRepository.findByUserUserIdAndBackgroundBackgroundId(userId, backgroundId)
                    .orElseThrow(() -> new EntityNotFoundException("Фон профиля не найден у пользователя"));
            isActive = userBackground.getIsActive();
        }
        
        return backgroundMapper.toModelWithUserInfo(background, isPurchased, isActive);
    }

    @Override
    @Transactional
    public ProfileBackgroundResponse purchaseBackground(Integer backgroundId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        ProfileBackground background = backgroundRepository.findById(backgroundId)
                .orElseThrow(() -> new EntityNotFoundException("Фон профиля не найден"));
        
        if (!background.getIsActive()) {
            throw new IllegalStateException("Фон профиля недоступен для покупки");
        }
        
        // Проверяем, не куплен ли уже фон
        if (userBackgroundRepository.existsByUserUserIdAndBackgroundBackgroundId(userId, backgroundId)) {
            throw new IllegalStateException("Фон профиля уже куплен");
        }
        
        // Списываем валюту
        currencyService.deductCurrency(
                userId, 
                background.getPrice(), 
                "Покупка фона профиля: " + background.getName(), 
                "purchase_profile_background", 
                backgroundId
        );
        
        // Создаем запись о покупке
        UserProfileBackground userBackground = new UserProfileBackground()
                .setUser(user)
                .setBackground(background)
                .setPurchasedAt(LocalDateTime.now())
                .setIsActive(false);
        
        userBackgroundRepository.save(userBackground);
        
        log.info("Пользователь с ID {} купил фон профиля с ID {}", userId, backgroundId);
        
        return backgroundMapper.toModelWithUserInfo(background, true, false);
    }

    @Override
    @Transactional
    public ProfileBackgroundResponse setBackgroundActive(Integer backgroundId, Integer userId, Boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        ProfileBackground background = backgroundRepository.findById(backgroundId)
                .orElseThrow(() -> new EntityNotFoundException("Фон профиля не найден"));
        
        UserProfileBackground userBackground = userBackgroundRepository.findByUserUserIdAndBackgroundBackgroundId(userId, backgroundId)
                .orElseThrow(() -> new EntityNotFoundException("Фон профиля не куплен пользователем"));
        
        if (active) {
            // Деактивируем все активные фоны пользователя
            userBackgroundRepository.deactivateAllUserBackgrounds(userId);
            
            // Активируем выбранный фон
            userBackground.setIsActive(true);
            userBackgroundRepository.save(userBackground);
            
            log.info("Пользователь с ID {} активировал фон профиля с ID {}", userId, backgroundId);
        } else {
            // Деактивируем выбранный фон
            userBackground.setIsActive(false);
            userBackgroundRepository.save(userBackground);
            
            log.info("Пользователь с ID {} деактивировал фон профиля с ID {}", userId, backgroundId);
        }
        
        return backgroundMapper.toModelWithUserInfo(background, true, active);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileBackgroundResponse getCurrentActiveBackground(Integer userId) {
        return userBackgroundRepository.findByUserUserIdAndIsActiveTrue(userId)
                .map(userBackground -> backgroundMapper.toModelWithUserInfo(userBackground.getBackground(), true, true))
                .orElse(null);
    }
}
