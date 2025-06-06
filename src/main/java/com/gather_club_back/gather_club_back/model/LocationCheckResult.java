package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LocationCheckResult {
    private boolean success;
    private boolean nearMeetup;
    private boolean nearParticipants;
    private boolean alreadyRewarded;
    private Integer rewardAmount;
    private Integer newBalance;
    private String message;
} 