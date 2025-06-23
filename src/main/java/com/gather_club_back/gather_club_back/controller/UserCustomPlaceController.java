package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserCustomPlaceResponse;
import com.gather_club_back.gather_club_back.service.UserCustomPlaceService;
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
@RequestMapping("/user-places")
@RequiredArgsConstructor
@Tag(name = "Пользовательские места", description = "API для управления пользовательскими местами")
public class UserCustomPlaceController {

    private final UserCustomPlaceService userCustomPlaceService;

    @Operation(summary = "Создать пользовательское место", description = "Создает новое пользовательское место для указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Место успешно создано", 
                content = @Content(schema = @Schema(implementation = UserCustomPlaceResponse.class)))
    @PostMapping("/{userId}")
    public ResponseEntity<UserCustomPlaceResponse> createPlace(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные места", required = true) @RequestBody UserCustomPlaceResponse place) {
        return ResponseEntity.ok(userCustomPlaceService.createPlace(userId, place));
    }

    @Operation(summary = "Обновить пользовательское место", description = "Обновляет существующее пользовательское место")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Место успешно обновлено", 
                    content = @Content(schema = @Schema(implementation = UserCustomPlaceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @PutMapping("/{userId}/{placeId}")
    public ResponseEntity<UserCustomPlaceResponse> updatePlace(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "Обновленные данные места", required = true) @RequestBody UserCustomPlaceResponse place) {
        return ResponseEntity.ok(userCustomPlaceService.updatePlace(userId, placeId, place));
    }

    @Operation(summary = "Удалить пользовательское место", description = "Удаляет пользовательское место")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Место успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @DeleteMapping("/{userId}/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        userCustomPlaceService.deletePlace(userId, placeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все пользовательские места", description = "Возвращает список всех пользовательских мест указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Список мест успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserCustomPlaceResponse.class))))
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCustomPlaceResponse>> getAllPlaces(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId) {
        return ResponseEntity.ok(userCustomPlaceService.getAllPlaces(userId));
    }

    @Operation(summary = "Получить места в указанной области", description = "Возвращает список пользовательских мест в заданной географической области")
    @ApiResponse(responseCode = "200", description = "Список мест успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserCustomPlaceResponse.class))))
    @GetMapping("/{userId}/area")
    public ResponseEntity<List<UserCustomPlaceResponse>> getPlacesInArea(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Минимальная широта", required = true) @RequestParam Double minLat,
            @Parameter(description = "Максимальная широта", required = true) @RequestParam Double maxLat,
            @Parameter(description = "Минимальная долгота", required = true) @RequestParam Double minLon,
            @Parameter(description = "Максимальная долгота", required = true) @RequestParam Double maxLon) {
        return ResponseEntity.ok(userCustomPlaceService.getPlacesInArea(userId, minLat, maxLat, minLon, maxLon));
    }

    @Operation(summary = "Получить места по категории", description = "Возвращает список пользовательских мест указанной категории")
    @ApiResponse(responseCode = "200", description = "Список мест успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserCustomPlaceResponse.class))))
    @GetMapping("/{userId}/category/{categoryId}")
    public ResponseEntity<List<UserCustomPlaceResponse>> getPlacesByCategory(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID категории", required = true) @PathVariable Integer categoryId) {
        return ResponseEntity.ok(userCustomPlaceService.getPlacesByCategory(userId, categoryId));
    }

    @Operation(summary = "Получить пользовательское место по ID", description = "Возвращает информацию о конкретном пользовательском месте")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Место успешно получено", 
                    content = @Content(schema = @Schema(implementation = UserCustomPlaceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @GetMapping("/{userId}/{placeId}")
    public ResponseEntity<UserCustomPlaceResponse> getPlace(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        return ResponseEntity.ok(userCustomPlaceService.getPlace(userId, placeId));
    }
}
