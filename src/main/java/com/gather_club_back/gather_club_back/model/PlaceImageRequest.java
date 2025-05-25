package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlaceImageRequest {
    private Integer placeId;
    private String imageUrl;
} 