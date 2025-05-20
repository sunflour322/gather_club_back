package com.gather_club_back.gather_club_back.model;

import com.gather_club_back.gather_club_back.enums.Role;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserResponse {
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "avata_url")
    private String avatarUrl;

    private String bio;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active")
    private Instant lastActive;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expires")
    private Instant resetTokenExpires;

    private Role role;
}
