package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class MeetupResponse {
    private Integer meetupId;
    private String name;
    private String description;
    private Integer placeId;
    private String placeName;
    private Integer creatorId;
    private String creatorName;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private String status;
    private List<ParticipantResponse> participants;
} 