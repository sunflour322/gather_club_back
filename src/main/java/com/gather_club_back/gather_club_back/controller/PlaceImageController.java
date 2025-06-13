package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceImageRequest;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
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
public class PlaceImageController {

    private final PlaceImageService placeImageService;

    @GetMapping("/place/{placeId}/image-url")
    public ResponseEntity<String> getPlaceImageUrl(@PathVariable Integer placeId) {
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

    @PostMapping("/place/{placeId}/image")
    public ResponseEntity<PlaceImageResponse> uploadPlaceImage(
            @PathVariable Integer placeId,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(required = false) Integer userId) {
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

    @PostMapping("/{userId}")
    public ResponseEntity<PlaceImageResponse> addImage(
            @PathVariable Integer userId,
            @RequestBody PlaceImageRequest request) {
        return ResponseEntity.ok(placeImageService.addImage(userId, request));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<PlaceImageResponse>> getPlaceImages(
            @PathVariable Integer placeId) {
        return ResponseEntity.ok(placeImageService.getPlaceImages(placeId));
    }

@PutMapping("/admin/approve/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> approveImage(
        @PathVariable Integer imageId) {
    placeImageService.approveImage(imageId);
    return ResponseEntity.ok().build();
}

@DeleteMapping("/admin/reject/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> rejectImage(
        @PathVariable Integer imageId) {
    placeImageService.rejectImage(imageId);
    return ResponseEntity.ok().build();
}

@PostMapping("/admin/moderate/{imageId}")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<Void> moderateImage(
        @PathVariable Integer imageId,
        @RequestParam Boolean approve) {
    if (approve) {
        placeImageService.approveImage(imageId);
    } else {
        placeImageService.rejectImage(imageId);
    }
    return ResponseEntity.ok().build();
}

@GetMapping("/admin/pending")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public ResponseEntity<List<PlaceImageResponse>> getPendingImages() {
    return ResponseEntity.ok(placeImageService.getPendingImages());
}

    @PostMapping("/{userId}/{imageId}/like")
    public ResponseEntity<Void> addLike(
            @PathVariable Integer userId,
            @PathVariable Integer imageId) {
        placeImageService.addLike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/{imageId}/dislike")
    public ResponseEntity<Void> addDislike(
            @PathVariable Integer userId,
            @PathVariable Integer imageId) {
        placeImageService.addDislike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{imageId}/like")
    public ResponseEntity<Void> removeLike(
            @PathVariable Integer userId,
            @PathVariable Integer imageId) {
        placeImageService.removeLike(userId, imageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/{imageId}/dislike")
    public ResponseEntity<Void> removeDislike(
            @PathVariable Integer userId,
            @PathVariable Integer imageId) {
        placeImageService.removeDislike(userId, imageId);
        return ResponseEntity.ok().build();
    }
}
