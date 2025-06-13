package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.ChatTheme;
import com.gather_club_back.gather_club_back.entity.UserChatTheme;
import com.gather_club_back.gather_club_back.model.ChatThemeResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatThemeMapper {
    
    /**
     * Преобразовать сущность ChatTheme в DTO ChatThemeResponse
     */
    public ChatThemeResponse toResponse(ChatTheme theme, boolean isPurchased, boolean isCurrentlyActive) {
        return new ChatThemeResponse()
                .setThemeId(theme.getThemeId())
                .setName(theme.getName())
                .setDescription(theme.getDescription())
                .setPrice(theme.getPrice())
                .setPreviewUrl(theme.getPreviewUrl())
                .setIsDefault(theme.getIsDefault())
                .setIsActive(theme.getIsActive())
                .setIsPurchased(isPurchased)
                .setIsCurrentlyActive(isCurrentlyActive);
    }
    
    /**
     * Преобразовать сущность UserChatTheme в DTO ChatThemeResponse
     */
    public ChatThemeResponse toResponse(UserChatTheme userTheme) {
        return toResponse(
                userTheme.getTheme(),
                true,
                userTheme.getIsActive()
        );
    }
    
    /**
     * Преобразовать список сущностей UserChatTheme в список DTO ChatThemeResponse
     */
    public List<ChatThemeResponse> toResponseList(List<UserChatTheme> userThemes) {
        return userThemes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
