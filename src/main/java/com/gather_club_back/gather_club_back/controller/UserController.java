package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserResponse;
import com.gather_club_back.gather_club_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем аутентифицированном пользователе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или не аутентифицирован")
    })
    @GetMapping("/current")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "Получить ID текущего пользователя", description = "Возвращает ID текущего аутентифицированного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "ID пользователя успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или не аутентифицирован")
    })
    @GetMapping("/current-id")
    public ResponseEntity<Integer> getCurrentUserId() {
        Integer userId = userService.getUserId();
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userId);
    }

    @Operation(summary = "Получить URL аватара пользователя", description = "Возвращает URL аватара пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL аватара успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или у него нет аватара")
    })
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<String> getAvatarUrl(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        try {
            String avatarUrl = userService.getAvatarUrl(userId);
            return ResponseEntity.ok(avatarUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить URL аватара текущего пользователя", description = "Возвращает URL аватара текущего аутентифицированного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL аватара успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден, не аутентифицирован или у него нет аватара")
    })
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

    @Operation(summary = "Обновить аватар пользователя", description = "Загружает новый аватар для пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Аватар успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректный файл аватара"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserResponse> updateUserAvatar(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Файл аватара", required = true) @RequestParam("avatar") MultipartFile avatarFile) {
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

    @Operation(summary = "Обновить аватар текущего пользователя", description = "Загружает новый аватар для текущего аутентифицированного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Аватар успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректный файл аватара"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/current/avatar")
    public ResponseEntity<?> updateCurrentUserAvatar(
            @Parameter(description = "Файл аватара", required = true) @RequestParam("avatar") MultipartFile avatarFile) {
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

    @Operation(summary = "Найти пользователя по email", description = "Возвращает пользователя с указанным email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "Email пользователя", required = true) @PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Найти пользователя по имени пользователя", description = "Возвращает пользователя с указанным именем пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> findByUsername(
            @Parameter(description = "Имя пользователя", required = true) @PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Установить статус пользователя 'онлайн'", description = "Устанавливает статус 'онлайн' для пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{userId}/status/online")
    public ResponseEntity<UserResponse> setUserOnline(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        try {
            UserResponse user = userService.setUserOnline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Установить статус пользователя 'оффлайн'", description = "Устанавливает статус 'оффлайн' для пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{userId}/status/offline")
    public ResponseEntity<UserResponse> setUserOffline(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        try {
            UserResponse user = userService.setUserOffline(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Установить статус текущего пользователя 'онлайн'", description = "Устанавливает статус 'онлайн' для текущего аутентифицированного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или не аутентифицирован")
    })
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
    
    @Operation(summary = "Установить статус текущего пользователя 'оффлайн'", description = "Устанавливает статус 'оффлайн' для текущего аутентифицированного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден или не аутентифицирован")
    })
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
