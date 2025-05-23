package com.gather_club_back.gather_club_back.mapper;


import com.gather_club_back.gather_club_back.entity.PlaceImage;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceImageMapper {
    public PlaceImageResponse toPlaceImageResponse(PlaceImage placeImage) {
        return new PlaceImageResponse()
                .setImageId(placeImage.getImageId())
                .setPlaceId(placeImage.getPlace().getPlaceId())
                .setImageUrl(placeImage.getImageUrl())
                .setUploadedBy(placeImage.getUploadedBy() != null ?
                        placeImage.getUploadedBy().getUserId() : null)
                .setUploadedAt(placeImage.getUploadedAt())
                .setIsApproved(placeImage.getIsApproved())
                .setLikes(placeImage.getLikes())
                .setDislikes(placeImage.getDislikes());
    }
}