package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.User;
import com.gather_club_back.gather_club_back.mapper.UserMapper;
import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.repository.UserRepository;
import com.gather_club_back.gather_club_back.service.UserService;
import com.gather_club_back.gather_club_back.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public UserResponse updateUserAvatar(Integer userId, MultipartFile avatarFile)  {
        validateAvatarFile(avatarFile);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        try {
            // Удаляем старый аватар, если он есть
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                try {
                    storageService.deleteImage(user.getAvatarUrl());
                    log.info("Удален старый аватар пользователя {}", userId);
                } catch (Exception e) {
                    log.warn("Не удалось удалить старый аватар пользователя {}: {}", userId, e.getMessage());
                    // Продолжаем выполнение даже если удаление не удалось
                }
            }

            // Генерируем уникальное имя файла
            String filename = generateAvatarFilename(avatarFile.getOriginalFilename(), userId);
            // Строим правильный путь к файлу
            String path = StorageService.ROOT_PATH + "/users/" + userId + "/avatar/" + filename;
            log.info("Сформирован путь для загрузки аватара: {}", path);

            String avatarUrl = null;
            
            try {
                // Пробуем загрузить через Firebase
                avatarUrl = storageService.uploadImage(avatarFile, path);
                log.info("Загружен новый аватар для пользователя {} по адресу {}", userId, avatarUrl);
            } catch (Exception e) {
                log.warn("Не удалось загрузить аватар в Firebase: {}", e.getMessage());
                
                // Если Firebase недоступен, сохраняем локально
                avatarUrl = saveAvatarLocally(avatarFile, userId, filename);
                log.info("Аватар сохранен локально для пользователя {}: {}", userId, avatarUrl);
            }

            // Обновляем пользователя
            user.setAvatarUrl(avatarUrl);
            User savedUser = userRepository.save(user);

            return userMapper.toUserResponse(savedUser);
        } catch (IOException e) {
            log.error("Ошибка обновления аватара пользователя {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить аватар пользователя", e);
        }
    }

    private String saveAvatarLocally(MultipartFile file, Integer userId, String filename) throws IOException {
        // Создаем директорию для аватаров пользователя
        Path userUploadDir = Paths.get(uploadDir, "users", userId.toString(), "avatar");
        Files.createDirectories(userUploadDir);
        
        // Сохраняем файл
        Path targetPath = userUploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Возвращаем локальный URL
        return "/api/uploads/users/" + userId + "/avatar/" + filename;
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
    
    @Override
    @Transactional
    public UserResponse setUserOnline(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setIsOnline(true);
        User savedUser = userRepository.save(user);
        log.info("User {} is now online", userId);
        
        return userMapper.toUserResponse(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponse setUserOffline(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setIsOnline(false);
        User savedUser = userRepository.save(user);
        log.info("User {} is now offline", userId);
        
        return userMapper.toUserResponse(savedUser);
    }

    private String generateAvatarFilename(String originalFilename, Integer userId) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            extension = ".jpg"; // дефолтное расширение, если не определено
        }
        return "user_" + userId + "_" + UUID.randomUUID() + extension;
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Ошибка валидации аватара: файл пуст или отсутствует");
            throw new IllegalArgumentException("Avatar file cannot be empty");
        }
        
        String contentType = file.getContentType();
        log.info("Проверка типа файла аватара: {}", contentType);
        
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            log.error("Ошибка валидации аватара: неверный тип содержимого - {}", contentType);
            throw new IllegalArgumentException("Only image files are allowed. Received: " + contentType);
        }
        
        log.info("Валидация аватара успешна, тип файла: {}, размер: {} байт", contentType, file.getSize());
    }
}