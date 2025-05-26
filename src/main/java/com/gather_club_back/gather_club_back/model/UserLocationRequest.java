package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLocationRequest {
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double altitude;
    private Boolean isPublic;
} 