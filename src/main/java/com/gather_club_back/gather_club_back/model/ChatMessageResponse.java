package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Integer messageId;
    private Integer chatId;
    private Integer senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Boolean isSystem;
    private Integer replyToId;
    private String replyToContent;
} 