package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ParticipantResponse {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private String status;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;
} 