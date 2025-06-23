package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.model.PlaceRequest;
import com.gather_club_back.gather_club_back.model.PlaceResponse;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
import com.gather_club_back.gather_club_back.service.PlaceService;
import com.gather_club_back.gather_club_back.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
@Tag(name = "Места", description = "API для управления местами встреч")
public class PlaceController {
    private final PlaceService placeService;
    private final PlaceImageService placeImageService;
    private final UserService userService;
    
    // CRUD операции для администратора
    
    @Operation(summary = "Получить все места", description = "Возвращает список всех мест (только для администраторов)")
    @ApiResponse(responseCode = "200", description = "Список мест успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceResponse.class))))
    @SecurityRequirement(name = "JWT")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PlaceResponse>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }
    
    @Operation(summary = "Получить место по ID", description = "Возвращает информацию о месте по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Место успешно найдено", 
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> getPlaceById(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        return ResponseEntity.ok(placeService.getPlaceById(placeId));
    }
    
    @Operation(summary = "Создать новое место", description = "Создает новое место на основе предоставленных данных (только для администраторов)")
    @ApiResponse(responseCode = "201", description = "Место успешно создано", 
                content = @Content(schema = @Schema(implementation = PlaceResponse.class)))
    @SecurityRequirement(name = "JWT")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PlaceResponse> createPlace(
            @Parameter(description = "Данные для создания места", required = true) @RequestBody PlaceRequest placeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placeService.createPlace(placeRequest));
    }
    
    @Operation(summary = "Обновить место", description = "Обновляет информацию о месте по указанному ID (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Место успешно обновлено", 
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{placeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PlaceResponse> updatePlace(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "Обновленные данные места", required = true) @RequestBody PlaceRequest placeRequest) {
        return ResponseEntity.ok(placeService.updatePlace(placeId, placeRequest));
    }
    
    @Operation(summary = "Удалить место", description = "Удаляет место по указанному ID (только для администраторов)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Место успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePlace(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить ближайшие места", description = "Возвращает список мест в указанном радиусе от заданных координат")
    @ApiResponse(responseCode = "200", description = "Список ближайших мест успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceResponse.class))))
    @GetMapping("/nearby")
    public List<PlaceResponse> getNearbyPlaces(
            @Parameter(description = "Широта", required = true) @RequestParam double lat,
            @Parameter(description = "Долгота", required = true) @RequestParam double lng,
            @Parameter(description = "Радиус поиска в километрах", required = false) @RequestParam(defaultValue = "5") double radiusKm) {

        return placeService.getNearbyPlaces(lat, lng, radiusKm);
    }

    @Operation(summary = "Обновить изображение места", description = "Загружает новое основное изображение для места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение успешно обновлено", 
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено"),
        @ApiResponse(responseCode = "400", description = "Ошибка при загрузке изображения")
    })
    @PostMapping("/{placeId}/image")
    public PlaceResponse updatePlaceImage(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "Файл изображения", required = true) @RequestParam("image") MultipartFile imageFile) {
        try {
            return placeService.updatePlaceImage(placeId, imageFile) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Operation(summary = "Получить изображения места", description = "Возвращает список всех изображений для указанного места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список изображений успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceImageResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @GetMapping("/{placeId}/images")
    public ResponseEntity<List<PlaceImageResponse>> getPlaceImages(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        return ResponseEntity.ok(placeImageService.getPlaceImages(placeId));
    }

    @Operation(summary = "Загрузить изображение места", description = "Загружает новое изображение для указанного места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение успешно загружено", 
                    content = @Content(schema = @Schema(implementation = PlaceImageResponse.class))),
        @ApiResponse(responseCode = "404", description = "Место не найдено"),
        @ApiResponse(responseCode = "400", description = "Ошибка при загрузке изображения")
    })
    @PostMapping("/{placeId}/images/add")
    public ResponseEntity<PlaceImageResponse> uploadPlaceImage(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "Файл изображения", required = true) @RequestParam("images") MultipartFile imageFile) throws IOException {

        Integer userId = userService.getUserId();
        return ResponseEntity.ok(
                placeImageService.uploadPlaceImage(placeId, imageFile, userId));
    }

    @Operation(summary = "Оценить изображение места", description = "Ставит лайк или дизлайк изображению места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Оценка успешно сохранена"),
        @ApiResponse(responseCode = "404", description = "Место или изображение не найдены")
    })
    @PostMapping("/{placeId}/images/{imageId}/rate")
    public ResponseEntity<Void> rateImage(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId,
            @Parameter(description = "Тип оценки (true - лайк, false - дизлайк)", required = true) @RequestParam Boolean isLike) {

        placeImageService.rateImage(imageId, isLike);
        return ResponseEntity.ok().build();
    }
}
