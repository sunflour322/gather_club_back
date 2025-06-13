package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlaceRequest {
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer categoryId;
    private Boolean isApproved;
    private Integer popularity;
}
