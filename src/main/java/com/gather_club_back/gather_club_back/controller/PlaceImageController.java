package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceImageRequest;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/place-images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Изображения мест", description = "API для управления изображениями мест")
public class PlaceImageController {

    private final PlaceImageService placeImageService;

    @Operation(summary = "Получить URL основного изображения места", description = "Возвращает URL основного изображения для указанного места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL изображения успешно получен"),
        @ApiResponse(responseCode = "404", description = "Место не найдено или у него нет изображений")
    })
    @GetMapping("/place/{placeId}/image-url")
    public ResponseEntity<String> getPlaceImageUrl(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        try {
            String imageUrl = placeImageService.getMainPlaceImageUrl(placeId);
            if (imageUrl == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(imageUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Загрузить изображение места", description = "Загружает новое изображение для указанного места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение успешно загружено", 
                    content = @Content(schema = @Schema(implementation = PlaceImageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный файл изображения"),
        @ApiResponse(responseCode = "404", description = "Место не найдено"),
        @ApiResponse(responseCode = "500", description = "Ошибка при загрузке изображения")
    })
    @PostMapping("/place/{placeId}/image")
    public ResponseEntity<PlaceImageResponse> uploadPlaceImage(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId,
            @Parameter(description = "Файл изображения", required = true) @RequestParam("image") MultipartFile imageFile,
            @Parameter(description = "ID пользователя, загружающего изображение", required = false) @RequestParam(required = false) Integer userId) {
        try {
            log.info("Получен запрос на загрузку изображения для места: {}, имя файла: {}, тип содержимого: {}, размер: {} байт",
                    placeId, imageFile.getOriginalFilename(), imageFile.getContentType(), imageFile.getSize());
            PlaceImageResponse response = placeImageService.uploadPlaceImage(placeId, imageFile, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации изображения: {}", e.getMessage());
            return ResponseEntity.status(400).body(null);
        } catch (Exception e) {
            log.error("Ошибка загрузки изображения для места {}: {}", placeId, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Добавить изображение по URL", description = "Добавляет новое изображение места по указанному URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Изображение успешно добавлено", 
                    content = @Content(schema = @Schema(implementation = PlaceImageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь или место не найдены")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<PlaceImageResponse> addImage(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "Данные изображения", required = true) @RequestBody PlaceImageRequest request) {
        return ResponseEntity.ok(placeImageService.addImage(userId, request));
    }

    @Operation(summary = "Получить изображения места", description = "Возвращает список всех изображений для указанного места")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список изображений успешно получен", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceImageResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Место не найдено")
    })
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<PlaceImageResponse>> getPlaceImages(
            @Parameter(description = "ID места", required = true) @PathVariable Integer placeId) {
        return ResponseEntity.ok(placeImageService.getPlaceImages(placeId));
    }

@Operation(summary = "Одобрить изображение", description = "Одобряет изображение для публичного просмотра (только для администраторов)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Изображение успешно одобрено"),
    @ApiResponse(responseCode = "403", description = "Недостаточно прав для одобрения изображения"),
    @ApiResponse(responseCode = "404", description = "Изображение не найдено")
})
@SecurityRequirement(name = "JWT")
@PutMapping("/admin/approve/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> approveImage(
        @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
    placeImageService.approveImage(imageId);
    return ResponseEntity.ok().build();
}

@Operation(summary = "Отклонить изображение", description = "Отклоняет изображение и удаляет его (только для администраторов)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Изображение успешно отклонено"),
    @ApiResponse(responseCode = "403", description = "Недостаточно прав для отклонения изображения"),
    @ApiResponse(responseCode = "404", description = "Изображение не найдено")
})
@SecurityRequirement(name = "JWT")
@DeleteMapping("/admin/reject/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> rejectImage(
        @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
    placeImageService.rejectImage(imageId);
    return ResponseEntity.ok().build();
}

@Operation(summary = "Модерировать изображение", description = "Одобряет или отклоняет изображение (только для администраторов)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Изображение успешно модерировано"),
    @ApiResponse(responseCode = "403", description = "Недостаточно прав для модерации изображения"),
    @ApiResponse(responseCode = "404", description = "Изображение не найдено")
})
@SecurityRequirement(name = "JWT")
@PostMapping("/admin/moderate/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> moderateImage(
        @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId,
        @Parameter(description = "Флаг одобрения (true - одобрить, false - отклонить)", required = true) @RequestParam Boolean approve) {
    if (approve) {
        placeImageService.approveImage(imageId);
    } else {
        placeImageService.rejectImage(imageId);
    }
    return ResponseEntity.ok().build();
}

@Operation(summary = "Получить ожидающие модерации изображения", description = "Возвращает список всех изображений, ожидающих модерации (только для администраторов)")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Список изображений успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceImageResponse.class)))),
    @ApiResponse(responseCode = "403", description = "Недостаточно прав для просмотра ожидающих изображений")
})
@SecurityRequirement(name = "JWT")
@GetMapping("/admin/pending")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<List<PlaceImageResponse>> getPendingImages() {
    return ResponseEntity.ok(placeImageService.getPendingImages());
}

    @Operation(summary = "Поставить лайк изображению", description = "Добавляет лайк от пользователя к указанному изображению")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лайк успешно добавлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь или изображение не найдены")
    })
    @PostMapping("/{userId}/{imageId}/like")
    public ResponseEntity<Void> addLike(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
        placeImageService.addLike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поставить дизлайк изображению", description = "Добавляет дизлайк от пользователя к указанному изображению")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Дизлайк успешно добавлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь или изображение не найдены")
    })
    @PostMapping("/{userId}/{imageId}/dislike")
    public ResponseEntity<Void> addDislike(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
        placeImageService.addDislike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Убрать лайк с изображения", description = "Удаляет лайк пользователя с указанного изображения")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лайк успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь, изображение или лайк не найдены")
    })
    @DeleteMapping("/{userId}/{imageId}/like")
    public ResponseEntity<Void> removeLike(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
        placeImageService.removeLike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Убрать дизлайк с изображения", description = "Удаляет дизлайк пользователя с указанного изображения")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Дизлайк успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь, изображение или дизлайк не найдены")
    })
    @DeleteMapping("/{userId}/{imageId}/dislike")
    public ResponseEntity<Void> removeDislike(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer userId,
            @Parameter(description = "ID изображения", required = true) @PathVariable Integer imageId) {
        placeImageService.removeDislike(userId, imageId);
        return ResponseEntity.ok().build();
    }
}
