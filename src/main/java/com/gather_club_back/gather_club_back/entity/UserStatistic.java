package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "user_statistics")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Integer statId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "matches_count")
    private Integer matchesCount = 0;

    @Column(name = "meetups_created")
    private Integer meetupsCreated = 0;

    @Column(name = "meetups_attended")
    private Integer meetupsAttended = 0;

    @Column(name = "friends_count")
    private Integer friendsCount = 0;

    @Column(name = "messages_sent")
    private Integer messagesSent = 0;

    @Column(name = "places_added")
    private Integer placesAdded = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
