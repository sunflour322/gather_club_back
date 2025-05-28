package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetupRequest {
    private Integer creatorId;
    private Integer placeId;
    private String name;
    private String description;
    private LocalDateTime scheduledTime;
    private List<Integer> invitedUserIds;
} 