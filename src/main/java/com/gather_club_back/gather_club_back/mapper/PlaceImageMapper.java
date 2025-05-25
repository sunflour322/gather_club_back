package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.PlaceImage;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceImageMapper {
    
    public PlaceImageResponse toModel(PlaceImage entity) {
        if (entity == null) {
            return null;
        }
        
        return new PlaceImageResponse()
                .setImageId(entity.getImageId())
                .setPlaceId(entity.getPlace().getPlaceId())
                .setImageUrl(entity.getImageUrl())
                .setUploadedById(entity.getUploadedBy().getUserId())
                .setUploaderUsername(entity.getUploadedBy().getUsername())
                .setUploadedAt(entity.getUploadedAt())
                .setIsApproved(entity.getIsApproved())
                .setLikes(entity.getLikes())
                .setDislikes(entity.getDislikes());
    }
}