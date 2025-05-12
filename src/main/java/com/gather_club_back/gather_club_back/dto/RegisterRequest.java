package com.gather_club_back.gather_club_back.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
}
