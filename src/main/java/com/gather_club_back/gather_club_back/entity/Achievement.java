package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(schema = "public", name = "achievements")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Integer achievementId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    private String criteria; // В реальном проекте можно использовать JSONB
    private Integer reward = 0;
}
