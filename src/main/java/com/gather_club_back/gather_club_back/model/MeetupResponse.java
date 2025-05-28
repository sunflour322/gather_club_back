package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetupResponse {
    private Integer meetupId;
    private String name;
    private String description;
    private PlaceResponse place;
    private UserResponse creator;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private String status;
    private List<MeetupParticipantResponse> participants;
} 