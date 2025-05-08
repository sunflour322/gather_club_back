package com.gather_club_back.gather_club_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(schema = "public", name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    private String username;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "avatar_url")
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
