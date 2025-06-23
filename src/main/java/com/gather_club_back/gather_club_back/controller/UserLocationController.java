package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserLocationRequest;
import com.gather_club_back.gather_club_back.model.UserLocationResponse;
import com.gather_club_back.gather_club_back.service.UserLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/location")
@RequiredArgsConstructor
@Tag(name = "Местоположение пользователя", description = "API для управления данными о местоположении пользователя")
public class UserLocationController {

    private final UserLocationService userLocationService;

    @Operation(summary = "Обновить местоположение пользователя", description = "Обновляет информацию о текущем местоположении пользователя")
    @ApiResponse(responseCode = "200", description = "Местоположение успешно обновлено", 
                content = @Content(schema = @Schema(implementation = UserLocationResponse.class)))
    @PostMapping
    public ResponseEntity<UserLocationResponse> updateLocation(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные о местоположении", required = true) @RequestBody UserLocationRequest request) {
        return ResponseEntity.ok(userLocationService.updateLocation(userId, request));
    }

    @Operation(summary = "Получить историю местоположений пользователя", description = "Возвращает список всех сохраненных местоположений пользователя")
    @ApiResponse(responseCode = "200", description = "Список местоположений успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserLocationResponse.class))))
    @GetMapping
    public ResponseEntity<List<UserLocationResponse>> getUserLocations(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(userLocationService.getUserLocations(userId));
    }

    @Operation(summary = "Получить последнее местоположение пользователя", description = "Возвращает информацию о последнем известном местоположении пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Местоположение успешно получено", 
                    content = @Content(schema = @Schema(implementation = UserLocationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Местоположение не найдено")
    })
    @GetMapping("/last")
    public ResponseEntity<UserLocationResponse> getLastLocation(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return userLocationService.getLastLocation(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить публичные местоположения пользователя", description = "Возвращает список публичных местоположений пользователя")
    @ApiResponse(responseCode = "200", description = "Список публичных местоположений успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserLocationResponse.class))))
    @GetMapping("/public")
    public ResponseEntity<List<UserLocationResponse>> getPublicLocations(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(userLocationService.getPublicLocations(userId));
    }

    @Operation(summary = "Получить местоположения друзей", description = "Возвращает список последних известных местоположений друзей пользователя")
    @ApiResponse(responseCode = "200", description = "Список местоположений друзей успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserLocationResponse.class))))
    @GetMapping("/friends")
    public ResponseEntity<List<UserLocationResponse>> getFriendsLastLocations(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(userLocationService.getFriendsLastLocations(userId));
    }
}
