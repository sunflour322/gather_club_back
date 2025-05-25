package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.UserCustomPlace;
import com.gather_club_back.gather_club_back.model.UserCustomPlaceResponse;
import org.springframework.stereotype.Component;

@Component
public class UserCustomPlaceMapper {
    
    public UserCustomPlaceResponse toModel(UserCustomPlace entity) {
        if (entity == null) {
            return null;
        }
        
        return new UserCustomPlaceResponse()
                .setPlaceId(entity.getPlaceId())
                .setUserId(entity.getUser().getUserId())
                .setName(entity.getName())
                .setDescription(entity.getDescription())
                .setLatitude(entity.getLatitude())
                .setLongitude(entity.getLongitude())
                .setCreatedAt(entity.getCreatedAt())
                .setCategoryId(entity.getCategory() != null ? entity.getCategory().getCategoryId() : null)
                .setImageUrl(entity.getImageUrl());
    }
} 