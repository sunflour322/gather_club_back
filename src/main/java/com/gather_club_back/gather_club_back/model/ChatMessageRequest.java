package com.gather_club_back.gather_club_back.model;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Integer chatId;
    private Integer senderId;
    private String content;
    private Integer replyToId;
} 