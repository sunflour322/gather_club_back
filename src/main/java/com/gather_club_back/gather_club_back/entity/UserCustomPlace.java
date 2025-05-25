package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "user_custom_places")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserCustomPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Integer placeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PlaceCategory category;

    @Column(name = "image_url")
    private String imageUrl;
} 