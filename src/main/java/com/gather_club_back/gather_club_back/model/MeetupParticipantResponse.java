package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MeetupParticipantResponse {
    private Integer participantId;
    private Integer meetupId;
    private UserResponse user;
    private String status;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;
} 