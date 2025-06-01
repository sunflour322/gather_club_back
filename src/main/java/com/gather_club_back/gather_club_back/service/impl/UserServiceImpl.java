package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.UserMapper;
import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserService;
import com.gather_club_back.gather_club_back.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    @Override
    @Transactional
    public UserResponse updateUserAvatar(Integer userId, MultipartFile avatarFile)  {
        validateAvatarFile(avatarFile);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        try {
            // Удаляем старый аватар, если он есть
            if (user.getAvatarUrl() != null) {
                storageService.deleteImage(user.getAvatarUrl());
                log.info("Deleted old avatar for user {}", userId);
            }

            // Генерируем уникальное имя файла
            String filename = generateAvatarFilename(avatarFile.getOriginalFilename(), userId);
            String path = StorageService.ROOT_PATH + "/users/" + userId + "/avatar/" + filename;

            // Загружаем новый аватар
            String avatarUrl = storageService.uploadImage(avatarFile, path);
            log.info("Uploaded new avatar for user {} to {}", userId, avatarUrl);

            // Обновляем пользователя
            user.setAvatarUrl(avatarUrl);
            User savedUser = userRepository.save(user);

            return userMapper.toUserResponse(savedUser);
        } catch (IOException e) {
            log.error("Failed to update avatar for user {}", userId, e);
            throw new RuntimeException("Failed to update user avatar", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getAvatarUrl(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getAvatarUrl)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String username = ((org.springframework.security.core.userdetails.UserDetails)
                authentication.getPrincipal()).getUsername();

        return userRepository.findByUsername(username)
                .map(User::getUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Integer userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    private String generateAvatarFilename(String originalFilename, Integer userId) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return "user_" + userId + "_" + UUID.randomUUID() + extension;
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }
}