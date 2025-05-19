package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.UserMapper;
import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserService;
import com.gather_club_back.gather_club_back.service.YandexDiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final YandexDiskService yandexDiskService;
    @Override
    public UserResponse updateUserAvatar(Integer userId, MultipartFile avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Удаляем старый аватар, если он есть
        if (user.getAvatarUrl() != null) {
            yandexDiskService.deleteImage(user.getAvatarUrl());
        }

        // Загружаем новый аватар
        String path = "users/" + userId + "/avatar/" + avatarFile.getOriginalFilename();
        String avatarUrl = yandexDiskService.uploadImage(avatarFile, path);

        // Обновляем пользователя
        user.setAvatarUrl(avatarUrl);
        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public String getAvatarUrl(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAvatarUrl() == null) {
            throw new RuntimeException("Avatar not set");
        }

        return user.getAvatarUrl();
    }
    @Override
    public UserResponse getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return userMapper.toUserResponse(user);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserResponse);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toUserResponse);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
