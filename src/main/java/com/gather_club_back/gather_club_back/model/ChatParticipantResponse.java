package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatParticipantResponse {
    private Integer participantId;
    private Integer chatId;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private String role;
} 