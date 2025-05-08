package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.model.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user){
        return new UserResponse()
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPasswordHash(user.getPasswordHash())
                .setPhoneNumber(user.getPhoneNumber())
                .setAvatarUrl(user.getAvatarUrl())
                .setBio(user.getBio())
                .setCreatedAt(user.getCreatedAt())
                .setLastActive(user.getLastActive())
                .setIsVerified(user.getIsVerified())
                .setVerificationToken(user.getVerificationToken())
                .setResetToken(user.getResetToken())
                .setResetTokenExpires(user.getResetTokenExpires());
    }
}
