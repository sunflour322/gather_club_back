package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ChatParticipantRequest {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private String role;
    private LocalDateTime joinedAt;
} 