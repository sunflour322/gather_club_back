package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profile_frames")
@Accessors(chain = true)
public class UserProfileFrame {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_frame_id")
    private Integer userFrameId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_id", nullable = false)
    private ProfileFrame frame;
    
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt = LocalDateTime.now();
    
    @Column(name = "is_active")
    private Boolean isActive = false;
}
