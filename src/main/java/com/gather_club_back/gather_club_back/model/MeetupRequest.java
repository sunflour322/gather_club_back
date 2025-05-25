package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class MeetupRequest {
    private String name;
    private String description;
    private Integer placeId;
    private LocalDateTime scheduledTime;
    private List<Integer> invitedUserIds;
} 