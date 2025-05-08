package com.gather_club_back.gather_club_back.model;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponse {
    private String username;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "avata_url")
    private String avatarUrl;

    private String bio;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "last_active")
    private String lastActive;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expires")
    private String resetTokenExpires;
}
