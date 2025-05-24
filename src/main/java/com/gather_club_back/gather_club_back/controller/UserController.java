package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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
            UserResponse updatedUser = userService.updateUserAvatar(userId, avatarFile);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/current/avatar")
    public ResponseEntity<UserResponse> updateCurrentUserAvatar(
            @RequestParam("avatar") MultipartFile avatarFile) {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            UserResponse updatedUser = userService.updateUserAvatar(userId, avatarFile);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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
}