package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.PlaceCategory;
import com.gather_club_back.gather_club_back.model.PlaceCategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceCategoryMapper {
    
    public PlaceCategoryResponse toModel(PlaceCategory entity) {
        if (entity == null) {
            return null;
        }
        
        return new PlaceCategoryResponse()
                .setCategoryId(entity.getCategoryId())
                .setName(entity.getName())
                .setIconUrl(entity.getIconUrl())
                .setIsActive(entity.getIsActive());
    }
} 