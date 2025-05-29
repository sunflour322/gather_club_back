package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatResponse {
    private Integer chatId;
    private String name;
    private Integer createdById;
    private String createdByName;
    private String createdByAvatar;
    private LocalDateTime createdAt;
    private Boolean isGroup;
    private Integer themeId;
    private Integer meetupId;
    private LocalDateTime lastMessageAt;
    private String lastMessageContent;
    private Integer unreadCount;
} 