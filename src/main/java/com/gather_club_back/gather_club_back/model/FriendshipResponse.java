package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class FriendshipResponse {
    private Integer friendshipId;
    private Integer user1Id;
    private Integer user2Id;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 