package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserLocationResponse {
    private Integer locationId;
    private Integer userId;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double altitude;
    private LocalDateTime timestamp;
    private Boolean isPublic;
} 