package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.model.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse getUser(Integer userId);
    Optional<UserResponse> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<UserResponse> findByUsername(String username);
    Boolean existsByUsername(String username);
    UserResponse updateUserAvatar(Integer userId, MultipartFile avatarFile);
    String getAvatarUrl(Integer userId);
    Integer getUserId();
    List<UserResponse> getAllUsers();
    UserResponse setUserOnline(Integer userId);
    UserResponse setUserOffline(Integer userId);
}
