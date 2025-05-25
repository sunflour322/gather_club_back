package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.UserCustomPlaceResponse;
import com.gather_club_back.gather_club_back.service.UserCustomPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-places")
@RequiredArgsConstructor
public class UserCustomPlaceController {

    private final UserCustomPlaceService userCustomPlaceService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserCustomPlaceResponse> createPlace(
            @PathVariable Integer userId,
            @RequestBody UserCustomPlaceResponse place) {
        return ResponseEntity.ok(userCustomPlaceService.createPlace(userId, place));
    }

    @PutMapping("/{userId}/{placeId}")
    public ResponseEntity<UserCustomPlaceResponse> updatePlace(
            @PathVariable Integer userId,
            @PathVariable Integer placeId,
            @RequestBody UserCustomPlaceResponse place) {
        return ResponseEntity.ok(userCustomPlaceService.updatePlace(userId, placeId, place));
    }

    @DeleteMapping("/{userId}/{placeId}")
    public ResponseEntity<Void> deletePlace(
            @PathVariable Integer userId,
            @PathVariable Integer placeId) {
        userCustomPlaceService.deletePlace(userId, placeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCustomPlaceResponse>> getAllPlaces(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(userCustomPlaceService.getAllPlaces(userId));
    }

    @GetMapping("/{userId}/area")
    public ResponseEntity<List<UserCustomPlaceResponse>> getPlacesInArea(
            @PathVariable Integer userId,
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        return ResponseEntity.ok(userCustomPlaceService.getPlacesInArea(userId, minLat, maxLat, minLon, maxLon));
    }

    @GetMapping("/{userId}/category/{categoryId}")
    public ResponseEntity<List<UserCustomPlaceResponse>> getPlacesByCategory(
            @PathVariable Integer userId,
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(userCustomPlaceService.getPlacesByCategory(userId, categoryId));
    }

    @GetMapping("/{userId}/{placeId}")
    public ResponseEntity<UserCustomPlaceResponse> getPlace(
            @PathVariable Integer userId,
            @PathVariable Integer placeId) {
        return ResponseEntity.ok(userCustomPlaceService.getPlace(userId, placeId));
    }
} 