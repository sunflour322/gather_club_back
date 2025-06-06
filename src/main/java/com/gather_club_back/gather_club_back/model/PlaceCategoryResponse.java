package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlaceCategoryResponse {
    private Integer categoryId;
    private String name;
    private String iconUrl;
    private Boolean isActive;
} 