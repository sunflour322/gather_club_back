package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProfileFrameResponse {
    
    private Integer frameId;
    private String name;
    private String description;
    private Integer price;
    private String previewUrl;
    private Boolean isDefault;
    private Boolean isActive;
    private Boolean isPurchased;
    private Boolean isCurrentlyActive;
}
