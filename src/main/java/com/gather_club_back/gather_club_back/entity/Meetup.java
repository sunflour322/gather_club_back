package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "meetups")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetup_id")
    private Integer meetupId;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String status;
}