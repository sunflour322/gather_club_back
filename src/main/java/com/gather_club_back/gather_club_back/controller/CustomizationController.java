package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.ChatThemeResponse;
import com.gather_club_back.gather_club_back.model.ProfileBackgroundResponse;
import com.gather_club_back.gather_club_back.model.ProfileFrameResponse;
import com.gather_club_back.gather_club_back.service.ChatThemeService;
import com.gather_club_back.gather_club_back.service.ProfileBackgroundService;
import com.gather_club_back.gather_club_back.service.ProfileFrameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customization")
@RequiredArgsConstructor
@Tag(name = "Кастомизация", description = "API для управления элементами кастомизации профиля пользователя")
public class CustomizationController {

    private final ProfileFrameService profileFrameService;
    private final ProfileBackgroundService profileBackgroundService;
    private final ChatThemeService chatThemeService;

    @Operation(summary = "Получить активную рамку профиля", description = "Возвращает текущую активную рамку профиля пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Рамка профиля успешно получена", 
                    content = @Content(schema = @Schema(implementation = ProfileFrameResponse.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или у него нет активной рамки")
    })
    @GetMapping("/active/frame/{userId}")
    public ResponseEntity<ProfileFrameResponse> getActiveProfileFrame(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        ProfileFrameResponse activeFrame = profileFrameService.getCurrentActiveFrame(userId);
        return ResponseEntity.ok(activeFrame);
    }

    @Operation(summary = "Получить активный фон профиля", description = "Возвращает текущий активный фон профиля пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Фон профиля успешно получен", 
                    content = @Content(schema = @Schema(implementation = ProfileBackgroundResponse.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или у него нет активного фона")
    })
    @GetMapping("/active/background/{userId}")
    public ResponseEntity<ProfileBackgroundResponse> getActiveProfileBackground(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        ProfileBackgroundResponse activeBackground = profileBackgroundService.getCurrentActiveBackground(userId);
        return ResponseEntity.ok(activeBackground);
    }

    @Operation(summary = "Получить активную тему чата", description = "Возвращает текущую активную тему чата пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Тема чата успешно получена", 
                    content = @Content(schema = @Schema(implementation = ChatThemeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или у него нет активной темы чата")
    })
    @GetMapping("/active/chat-theme/{userId}")
    public ResponseEntity<ChatThemeResponse> getActiveChatTheme(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        ChatThemeResponse activeTheme = chatThemeService.getCurrentActiveTheme(userId);
        return ResponseEntity.ok(activeTheme);
    }
}
