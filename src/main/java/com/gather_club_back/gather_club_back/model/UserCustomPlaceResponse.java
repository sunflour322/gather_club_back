package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserCustomPlaceResponse {
    private Integer placeId;
    private Integer userId;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private Integer categoryId;
    private PlaceCategoryResponse category;
    private String imageUrl;
} 