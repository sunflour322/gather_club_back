package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "chats")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer chatId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_group")
    private Boolean isGroup;

    @Column(name = "theme_id")
    private Integer themeId;

    @ManyToOne
    @JoinColumn(name = "meetup_id")
    private Meetup meetup;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
}
