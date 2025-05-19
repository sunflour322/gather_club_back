package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Entity
@Table(schema = "public", name = "interesting_places")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "image_url")
    private String imageUrl;

}
