package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(schema = "public", name = "chat_themes")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ChatTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Integer themeId;

    @Column(nullable = false)
    private String name;

    private String description;
    private Integer price = 0;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

