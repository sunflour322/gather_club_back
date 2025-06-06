package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/current-id")
    public ResponseEntity<Integer> getCurrentUserId() {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<String> getAvatarUrl(@PathVariable Integer userId) {
        try {
            String avatarUrl = userService.getAvatarUrl(userId);
            return ResponseEntity.ok(avatarUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/current/avatar")
    public ResponseEntity<String> getCurrentUserAvatarUrl() {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String avatarUrl = userService.getAvatarUrl(userId);
            return ResponseEntity.ok(avatarUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserResponse> updateUserAvatar(
            @PathVariable Integer userId,
            @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            log.info("Получен запрос на обновление аватара для пользователя: {}, имя файла: {}, тип содержимого: {}, размер: {} байт",
                    userId, avatarFile.getOriginalFilename(), avatarFile.getContentType(), avatarFile.getSize());
            UserResponse updatedUser = userService.updateUserAvatar(userId, avatarFile);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Ошибка обновления аватара для пользователя {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/current/avatar")
    public ResponseEntity<?> updateCurrentUserAvatar(
            @RequestParam("avatar") MultipartFile avatarFile) {
        try {
        Integer userId = userService.getUserId();
        if (userId == null) {
                log.error("Невозможно обновить аватар: пользователь не найден");
                return ResponseEntity.status(401).body("Пользователь не авторизован");
        }
            
            log.info("Получен запрос на обновление аватара для текущего пользователя (ID: {}), имя файла: {}, тип содержимого: {}, размер: {} байт",
                    userId, avatarFile.getOriginalFilename(), avatarFile.getContentType(), avatarFile.getSize());
            
            UserResponse updatedUser = userService.updateUserAvatar(userId, avatarFile);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации аватара: {}", e.getMessage());
            return ResponseEntity.status(400).body("Некорректный файл аватара: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка обновления аватара для текущего пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/status/online")
    public ResponseEntity<UserResponse> setUserOnline(@PathVariable Integer userId) {
        try {
            UserResponse user = userService.setUserOnline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{userId}/status/offline")
    public ResponseEntity<UserResponse> setUserOffline(@PathVariable Integer userId) {
        try {
            UserResponse user = userService.setUserOffline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/current/status/online")
    public ResponseEntity<UserResponse> setCurrentUserOnline() {
        try {
            Integer userId = userService.getUserId();
            UserResponse user = userService.setUserOnline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/current/status/offline")
    public ResponseEntity<UserResponse> setCurrentUserOffline() {
        try {
            Integer userId = userService.getUserId();
            UserResponse user = userService.setUserOffline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}