package com.gather_club_back.gather_club_back.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PlaceImageResponse {
    private Integer imageId;
    private Integer placeId;
    private String imageUrl;
    private Integer uploadedBy;
    private LocalDateTime uploadedAt;
    private Boolean isApproved;
    private Integer likes;
    private Integer dislikes;
}
