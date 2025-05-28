package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.Place;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {
    public PlaceResponse toModel(Place entity) {
        if (entity == null) {
            return null;
        }

        PlaceResponse response = new PlaceResponse();
        response.setPlaceId(entity.getPlaceId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setAddress(entity.getAddress());
        response.setCategoryId(entity.getCategoryId());
        response.setCreatedAt(entity.getCreatedAt());
        response.setIsApproved(entity.getIsApproved());
        response.setPopularity(entity.getPopularity());
        response.setImageUrl(entity.getImageUrl());
        
        return response;
    }
}
