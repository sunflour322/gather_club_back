package com.gather_club_back.gather_club_back.model;

import lombok.Data;

@Data
public class ShopItemRequest {
    
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private Boolean isActive;
}
