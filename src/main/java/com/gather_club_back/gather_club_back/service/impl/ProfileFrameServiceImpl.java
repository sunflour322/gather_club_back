package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.ProfileFrame;
import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.entity.UserProfileFrame;
import com.gather_club_back.gather_club_back.mapper.ProfileFrameMapper;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import com.gather_club_back.gather_club_back.repository.ProfileFrameRepository;
import com.gather_club_back.gather_club_back.repository.UserProfileFrameRepository;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.ProfileFrameService;
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
public class ProfileFrameServiceImpl implements ProfileFrameService {

    private final ProfileFrameRepository frameRepository;
    private final UserProfileFrameRepository userFrameRepository;
    private final UserRepository userRepository;
    private final ProfileFrameMapper frameMapper;
    private final UserCurrencyService currencyService;

    @Override
    @Transactional(readOnly = true)
    public List<ProfileFrameResponse> getAllFrames(Integer userId) {
        List<ProfileFrame> frames = frameRepository.findByIsActiveTrue();
        List<UserProfileFrame> userFrames = userFrameRepository.findByUserUserId(userId);
        
        return frameMapper.toModelList(frames, userFrames);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileFrameResponse getFrameById(Integer frameId, Integer userId) {
        ProfileFrame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new EntityNotFoundException("Рамка профиля не найдена"));
        
        boolean isPurchased = userFrameRepository.existsByUserUserIdAndFrameFrameId(userId, frameId);
        boolean isActive = false;
        
        if (isPurchased) {
            UserProfileFrame userFrame = userFrameRepository.findByUserUserIdAndFrameFrameId(userId, frameId)
                    .orElseThrow(() -> new EntityNotFoundException("Рамка профиля не найдена у пользователя"));
            isActive = userFrame.getIsActive();
        }
        
        return frameMapper.toModelWithUserInfo(frame, isPurchased, isActive);
    }

    @Override
    @Transactional
    public ProfileFrameResponse purchaseFrame(Integer frameId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        ProfileFrame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new EntityNotFoundException("Рамка профиля не найдена"));
        
        if (!frame.getIsActive()) {
            throw new IllegalStateException("Рамка профиля недоступна для покупки");
        }
        
        // Проверяем, не куплена ли уже рамка
        if (userFrameRepository.existsByUserUserIdAndFrameFrameId(userId, frameId)) {
            throw new IllegalStateException("Рамка профиля уже куплена");
        }
        
        // Списываем валюту
        currencyService.deductCurrency(
                userId, 
                frame.getPrice(), 
                "Покупка рамки профиля: " + frame.getName(), 
                "purchase_profile_frame", 
                frameId
        );
        
        // Создаем запись о покупке
        UserProfileFrame userFrame = new UserProfileFrame()
                .setUser(user)
                .setFrame(frame)
                .setPurchasedAt(LocalDateTime.now())
                .setIsActive(false);
        
        userFrameRepository.save(userFrame);
        
        log.info("Пользователь с ID {} купил рамку профиля с ID {}", userId, frameId);
        
        return frameMapper.toModelWithUserInfo(frame, true, false);
    }

    @Override
    @Transactional
    public ProfileFrameResponse setFrameActive(Integer frameId, Integer userId, Boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        
        ProfileFrame frame = frameRepository.findById(frameId)
                .orElseThrow(() -> new EntityNotFoundException("Рамка профиля не найдена"));
        
        UserProfileFrame userFrame = userFrameRepository.findByUserUserIdAndFrameFrameId(userId, frameId)
                .orElseThrow(() -> new EntityNotFoundException("Рамка профиля не куплена пользователем"));
        
        if (active) {
            // Деактивируем все активные рамки пользователя
            userFrameRepository.deactivateAllUserFrames(userId);
            
            // Активируем выбранную рамку
            userFrame.setIsActive(true);
            userFrameRepository.save(userFrame);
            
            log.info("Пользователь с ID {} активировал рамку профиля с ID {}", userId, frameId);
        } else {
            // Деактивируем выбранную рамку
            userFrame.setIsActive(false);
            userFrameRepository.save(userFrame);
            
            log.info("Пользователь с ID {} деактивировал рамку профиля с ID {}", userId, frameId);
        }
        
        return frameMapper.toModelWithUserInfo(frame, true, active);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileFrameResponse getCurrentActiveFrame(Integer userId) {
        return userFrameRepository.findByUserUserIdAndIsActiveTrue(userId)
                .map(userFrame -> frameMapper.toModelWithUserInfo(userFrame.getFrame(), true, true))
                .orElse(null);
    }
}
