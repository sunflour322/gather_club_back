package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.UserLocation;
import com.gather_club_back.gather_club_back.model.UserLocationResponse;
import org.springframework.stereotype.Component;

@Component
public class UserLocationMapper {
    
    public UserLocationResponse toModel(UserLocation entity) {
        return new UserLocationResponse()
                .setLocationId(entity.getLocationId())
                .setUserId(entity.getUser().getUserId())
                .setLatitude(entity.getLatitude())
                .setLongitude(entity.getLongitude())
                .setAccuracy(entity.getAccuracy())
                .setAltitude(entity.getAltitude())
                .setTimestamp(entity.getTimestamp())
                .setIsPublic(entity.getIsPublic());
    }
} 