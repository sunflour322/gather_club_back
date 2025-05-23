package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "user_chat_themes")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserChatTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_theme_id")
    private Integer userThemeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private ChatTheme theme;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "is_active")
    private Boolean isActive = false;
}
