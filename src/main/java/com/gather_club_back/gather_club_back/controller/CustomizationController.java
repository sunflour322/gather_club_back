package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatThemeResponse;
import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import com.gather_club_back.gather_club_back.service.ChatThemeService;
import com.gather_club_back.gather_club_back.service.ProfileBackgroundService;
import com.gather_club_back.gather_club_back.service.ProfileFrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customization")
@RequiredArgsConstructor
public class CustomizationController {

    private final ProfileFrameService profileFrameService;
    private final ProfileBackgroundService profileBackgroundService;
    private final ChatThemeService chatThemeService;

    /**
     * Получить активную рамку профиля пользователя
     */
    @GetMapping("/active/frame/{userId}")
    public ResponseEntity<ProfileFrameResponse> getActiveProfileFrame(@PathVariable Integer userId) {
        ProfileFrameResponse activeFrame = profileFrameService.getCurrentActiveFrame(userId);
        return ResponseEntity.ok(activeFrame);
    }

    /**
     * Получить активный фон профиля пользователя
     */
    @GetMapping("/active/background/{userId}")
    public ResponseEntity<ProfileBackgroundResponse> getActiveProfileBackground(@PathVariable Integer userId) {
        ProfileBackgroundResponse activeBackground = profileBackgroundService.getCurrentActiveBackground(userId);
        return ResponseEntity.ok(activeBackground);
    }

    /**
     * Получить активную тему чата пользователя
     */
    @GetMapping("/active/chat-theme/{userId}")
    public ResponseEntity<ChatThemeResponse> getActiveChatTheme(@PathVariable Integer userId) {
        ChatThemeResponse activeTheme = chatThemeService.getCurrentActiveTheme(userId);
        return ResponseEntity.ok(activeTheme);
    }
}
