package com.gather_club_back.gather_club_back.model;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class PlaceResponse {
    @Column(name = "place_id")
    private Integer placeId;

    private String name;

    private String description;

    private Double latitude;

    private Double longitude;

    private String address;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "is_approved")
    private Boolean isApproved;

    private Integer popularity;
}
