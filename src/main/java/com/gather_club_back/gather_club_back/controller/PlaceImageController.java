package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceImageRequest;
import com.gather_club_back.gather_club_back.model.PlaceImageResponse;
import com.gather_club_back.gather_club_back.service.PlaceImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place-images")
@RequiredArgsConstructor
public class PlaceImageController {

    private final PlaceImageService placeImageService;

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
    public ResponseEntity<Void> approveImage(
            @PathVariable Integer imageId) {
        placeImageService.approveImage(imageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/reject/{imageId}")
    public ResponseEntity<Void> rejectImage(
            @PathVariable Integer imageId) {
        placeImageService.rejectImage(imageId);
        return ResponseEntity.ok().build();
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