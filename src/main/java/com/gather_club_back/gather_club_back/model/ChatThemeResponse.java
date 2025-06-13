package com.gather_club_back.gather_club_back.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChatThemeResponse {
    
    private Integer themeId;
    private String name;
    private String description;
    private Integer price;
    private String previewUrl;
    private Boolean isDefault;
    private Boolean isActive;
    private Boolean isPurchased;
    private Boolean isCurrentlyActive;
}
