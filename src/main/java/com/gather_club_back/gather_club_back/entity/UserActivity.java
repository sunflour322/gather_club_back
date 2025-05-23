package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "user_activities")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Integer activityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "activity_data")
    private String activityData; // В реальном проекте можно использовать JSONB

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
