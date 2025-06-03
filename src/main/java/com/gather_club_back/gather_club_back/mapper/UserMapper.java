package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.model.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toModel(User entity) {
        if (entity == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setUserId(entity.getUserId());
        response.setUsername(entity.getUsername());
        response.setAvatarUrl(entity.getAvatarUrl());
        response.setIsOnline(entity.getIsOnline());
        
        return response;
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse()
                .setUserId(user.getUserId())
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
                .setResetTokenExpires(user.getResetTokenExpires())
                .setIsOnline(user.getIsOnline())
                .setRole(user.getRole());
    }

    public User toUser(UserResponse userResponse) {
        return new User()
                .setUserId(userResponse.getUserId())
                .setUsername(userResponse.getUsername())
                .setEmail(userResponse.getEmail())
                .setPasswordHash(userResponse.getPasswordHash())
                .setPhoneNumber(userResponse.getPhoneNumber())
                .setAvatarUrl(userResponse.getAvatarUrl())
                .setBio(userResponse.getBio())
                .setCreatedAt(userResponse.getCreatedAt())
                .setLastActive(userResponse.getLastActive())
                .setIsVerified(userResponse.getIsVerified())
                .setVerificationToken(userResponse.getVerificationToken())
                .setResetToken(userResponse.getResetToken())
                .setResetTokenExpires(userResponse.getResetTokenExpires())
                .setRole(userResponse.getRole());
    }
}
