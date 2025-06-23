package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profile_backgrounds")
@Accessors(chain = true)
public class UserProfileBackground {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_background_id")
    private Integer userBackgroundId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_id", nullable = false)
    private ProfileBackground background;
    
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt = LocalDateTime.now();
    
    @Column(name = "is_active")
    private Boolean isActive = false;
}
