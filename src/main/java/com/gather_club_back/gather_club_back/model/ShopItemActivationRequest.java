package com.gather_club_back.gather_club_back.model;

import lombok.Data;

@Data
public class ShopItemActivationRequest {
    
    private Integer itemId;
    private String itemType; // "chat_theme", "profile_frame", "profile_background"
    private Boolean activate; // true - активировать, false - деактивировать
}
